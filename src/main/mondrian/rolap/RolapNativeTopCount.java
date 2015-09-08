/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2004-2005 TONBELLER AG
// Copyright (C) 2005-2005 Julian Hyde
// Copyright (C) 2005-2013 Pentaho and others
// All Rights Reserved.
*/
package mondrian.rolap;

import mondrian.calc.TupleList;
import mondrian.mdx.MemberExpr;
import mondrian.olap.*;
import mondrian.rolap.aggmatcher.AggStar;
import mondrian.rolap.sql.*;
import mondrian.rolap.sql.query.*;
import mondrian.spi.Dialect;
import mondrian.spi.DialectManager;
import mondrian.util.Pair;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import static mondrian.rolap.sql.query.RightJoinBuilder.BASE_QUERY_ALIAS;
import static mondrian.rolap.sql.query.RightJoinBuilder.JOIN_QUERY_ALIAS;

/**
 * Computes a TopCount in SQL.
 *
 * @author av
 * @since Nov 21, 2005
  */
public class RolapNativeTopCount extends RolapNativeSet {

    public RolapNativeTopCount() {
        super.setEnabled(
                MondrianProperties.instance().EnableNativeTopCount.get());
    }

    static class TopCountConstraint extends SetConstraint {
        Exp orderByExpr;
        boolean ascending;
        Integer topCount;

        public TopCountConstraint(
            int count,
            CrossJoinArg[] args, RolapEvaluator evaluator,
            Exp orderByExpr, boolean ascending)
        {
            super(args, evaluator, true);
            this.orderByExpr = orderByExpr;
            this.ascending = ascending;
            this.topCount = count;
        }

        /**
         * {@inheritDoc}
         *
         * <p>TopCount always needs to join the fact table because we want to
         * evaluate the top count expression which involves a fact.
         */
        protected boolean isJoinRequired() {
            return true;
        }

        public void addConstraint(
            SqlQuery sqlQuery,
            RolapCube baseCube,
            AggStar aggStar)
        {
            if (orderByExpr != null) {
                RolapNativeSql sql =
                    new RolapNativeSql(
                        sqlQuery, aggStar, getEvaluator(), null);
                final String orderBySql =
                    sql.generateTopCountOrderBy(orderByExpr);
                boolean nullable =
                    deduceNullability(orderByExpr);
                final String orderByAlias =
                    sqlQuery.addSelect(orderBySql, null);
                sqlQuery.addOrderBy(
                    orderBySql,
                    orderByAlias,
                    ascending,
                    true,
                    nullable,
                    true);
            }
            super.addConstraint(sqlQuery, baseCube, aggStar);
        }

        private boolean deduceNullability(Exp expr) {
            if (!(expr instanceof MemberExpr)) {
                return true;
            }
            final MemberExpr memberExpr = (MemberExpr) expr;
            if (!(memberExpr.getMember() instanceof RolapStoredMeasure)) {
                return true;
            }
            final RolapStoredMeasure measure =
                (RolapStoredMeasure) memberExpr.getMember();
            return measure.getAggregator() != RolapAggregator.DistinctCount;
        }

        public Object getCacheKey() {
            List<Object> key = new ArrayList<Object>();
            key.add(super.getCacheKey());
            // Note: need to use string in order for caching to work
            if (orderByExpr != null) {
                key.add(orderByExpr.toString());
            }
            key.add(ascending);
            key.add(topCount);

            if (this.getEvaluator() instanceof RolapEvaluator) {
                key.add(
                    ((RolapEvaluator)this.getEvaluator())
                    .getSlicerMembers());
            }
            return key;
        }
    }

    protected boolean restrictMemberTypes() {
        return true;
    }

    NativeEvaluator createEvaluator(
        RolapEvaluator evaluator,
        FunDef fun,
        Exp[] args)
    {
        if (!isEnabled()) {
            return null;
        }

        if (!TopCountConstraint.isValidContext(
                evaluator, restrictMemberTypes()))
        {
            return null;
        }

        // is this "TopCount(<set>, <count>, [<numeric expr>])"
        String funName = fun.getName();
        final boolean ascending;
        if ("TopCount".equalsIgnoreCase(funName)) {
            ascending = false;
        } else if ("BottomCount".equalsIgnoreCase(funName)) {
            ascending = true;
        } else {
            return null;
        }
        if (args.length < 2 || args.length > 3) {
            return null;
        }

        // extract the set expression
        List<CrossJoinArg[]> allArgs =
            crossJoinArgFactory().checkCrossJoinArg(evaluator, args[0]);

        // checkCrossJoinArg returns a list of CrossJoinArg arrays.  The first
        // array is the CrossJoin dimensions.  The second array, if any,
        // contains additional constraints on the dimensions. If either the list
        // or the first array is null, then native cross join is not feasible.
        if (allArgs == null || allArgs.isEmpty() || allArgs.get(0) == null) {
            return null;
        }

        CrossJoinArg[] cjArgs = allArgs.get(0);
        if (isPreferInterpreter(cjArgs, false)) {
            return null;
        }

        // extract count
        if (!(args[1] instanceof Literal)) {
            return null;
        }
        int count = ((Literal) args[1]).getIntValue();

        // extract "order by" expression
        SchemaReader schemaReader = evaluator.getSchemaReader();
        DataSource ds = schemaReader.getDataSource();

        // generate the ORDER BY Clause
        // Need to generate top count order by to determine whether
        // or not it can be created. The top count
        // could change to use an aggregate table later in evaulation
        SqlQuery sqlQuery = SqlQuery.newQuery(ds, "NativeTopCount");
        RolapNativeSql sql =
            new RolapNativeSql(
                sqlQuery, null, evaluator, null);
        Exp orderByExpr = null;
        if (args.length == 3) {
            orderByExpr = args[2];
            String orderBySQL = sql.generateTopCountOrderBy(args[2]);
            if (orderBySQL == null) {
                return null;
            }
        }
        LOGGER.debug("using native topcount");
        final int savepoint = evaluator.savepoint();
        try {
            overrideContext(evaluator, cjArgs, sql.getStoredMeasure());

            CrossJoinArg[] predicateArgs = null;
            if (allArgs.size() == 2) {
                predicateArgs = allArgs.get(1);
            }

            CrossJoinArg[] combinedArgs;
            if (predicateArgs != null) {
                // Combined the CJ and the additional predicate args
                // to form the TupleConstraint.
                combinedArgs =
                        Util.appendArrays(cjArgs, predicateArgs);
            } else {
                combinedArgs = cjArgs;
            }
            TupleConstraint constraint =
                new TopCountConstraint(
                    count, combinedArgs, evaluator, orderByExpr, ascending);

            SetEvaluator sev;
            if (evaluator.isNonEmpty() && args.length == 3) {
                sev = new SetEvaluator(cjArgs, schemaReader, constraint);
            } else {
                sev = new NativeTopCountSetEvaluator(cjArgs, schemaReader, constraint);
            }
            sev.setMaxRows(count);
            sev.setNonEmpty(evaluator.isNonEmpty());
            return sev;
        } finally {
            evaluator.restore(savepoint);
        }
    }

    private class NativeTopCountSetEvaluator extends SetEvaluator {
        public NativeTopCountSetEvaluator(CrossJoinArg[] args, SchemaReader schemaReader, TupleConstraint constraint) {
            super(args, schemaReader, constraint);
        }

        @Override
        protected TupleList executeList(TupleConstraint constraint) {
            return executeList(new TopCountSqlTupleReader(constraint));
        }
    }

    private static class TopCountSqlTupleReader extends SqlTupleReader {
        public TopCountSqlTupleReader(TupleConstraint constraint) {
            super(constraint);
        }

        @Override
        protected Pair<String, List<SqlStatement.Type>> generateSelectForLevels(DataSource dataSource, RolapCube baseCube, WhichSelect whichSelect) {
            SqlQueryBuilder subselect = new SqlQueryBuilder(DialectManager.createDialect(dataSource, null));
            subselect.setAllowHints(true);
            super.generateSelectForLevels(baseCube, whichSelect, subselect, constraint);

            Evaluator evaluator = getEvaluator(constraint);
            AggStar aggStar = chooseAggStar(constraint, evaluator, baseCube);

            CrossJoinBuilder builder = CrossJoinBuilder.builder(subselect.getDialect());

            for (TargetBase target : targets) {
                if (target.getSrcMembers() == null) {
                    SqlQueryBuilder crossJoinQuery = new SqlQueryBuilder(subselect.getDialect());
                    addLevelMemberSql(
                            crossJoinQuery,
                            target.getLevel(),
                            baseCube,
                            whichSelect,
                            aggStar,
                            DefaultTupleConstraint.instance());
                    builder.append(crossJoinQuery);
                }
            }

            SqlSelect crossJoin = builder.build();
            if (crossJoin == null) {
                return subselect.toSqlAndTypes();
            }

            SqlSelect result = RightJoinBuilder.builder(subselect.getDialect())
                    .query(subselect.toSelect())
                    .rightJoin(crossJoin)
                .build(
                        new RightJoinBuilder.Populator() {
                            @Override
                            public void populateOutput(SqlQuery query, SqlSelect baseQuery, SqlSelect joinQuery) {
                                SqlUtils.addSelectFields(query, joinQuery.getOutputFields(), JOIN_QUERY_ALIAS);
                                int populated = joinQuery.getOutputFields().size();
                                int subQuerySize = baseQuery.getOutputFields().size();
                                if (populated < subQuerySize) {
                                    List<SelectElement> restFromBase = baseQuery.getOutputFields().subList(populated, subQuerySize);
                                    SqlUtils.addSelectFields(query, restFromBase, BASE_QUERY_ALIAS);
                                }
                            }
                        },
                        new RightJoinBuilder.Mapper() {
                            @Override
                            public String map(List<SelectElement> baseQueryOutput, List<SelectElement> joinQueryOutput, Dialect dialect) {
                                StringBuilder sb = new StringBuilder();
                                addEqualsCondition(sb, baseQueryOutput.get(0), joinQueryOutput.get(0), dialect);
                                for (int i = 1, len = joinQueryOutput.size(); i < len; i++) {
                                    sb.append(" and ");
                                    addEqualsCondition(sb, baseQueryOutput.get(i), joinQueryOutput.get(i), dialect);
                                }
                                return sb.toString();
                            }

                            private void addEqualsCondition(StringBuilder sb, SelectElement baseQueryOutput, SelectElement joinQueryOutput, Dialect dialect) {
                                sb.append('(')
                                    .append(dialect.quoteIdentifier(BASE_QUERY_ALIAS, baseQueryOutput.getAlias()))
                                    .append('=')
                                    .append(dialect.quoteIdentifier(JOIN_QUERY_ALIAS, joinQueryOutput.getAlias()))
                                .append(')');
                            }
                        });

            return Pair.of(result.getSql(), result.getOutputTypes());
        }
    }
}

// End RolapNativeTopCount.java
