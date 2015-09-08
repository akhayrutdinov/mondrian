package mondrian.rolap.sql.query;

import mondrian.olap.Util;
import mondrian.rolap.sql.SqlQuery;
import mondrian.spi.Dialect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey Khayrutdinov
 */
public class CrossJoinBuilder {

    public static CrossJoinBuilder builder(Dialect dialect) {
        return new CrossJoinBuilder(dialect);
    }


    private final List<SqlSelect> queries;
    private final Dialect dialect;

    public CrossJoinBuilder(Dialect dialect) {
        this.queries = new ArrayList<SqlSelect>(8);
        this.dialect = dialect;
    }

    public CrossJoinBuilder append(SqlQueryBuilder sqlQuery) {
        return append(sqlQuery.toSelect());
    }

    public CrossJoinBuilder append(SqlSelect select) {
        queries.add(select);
        return this;
    }

    public SqlSelect build() {
        int size = queries.size();
        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return queries.get(0);
        }

        AliasGenerator aliasGenerator = new AliasGenerator("t");
        SqlQueryBuilder query = new SqlQueryBuilder(dialect);

        SqlSelect select = queries.get(0);
        String alias = aliasGenerator.nextAlias();
        query.addFromQuery(select.getSql(), alias, true);

        SqlUtils.addSelectFields(query, select.getOutputFields(), alias);

        StringBuilder sb = new StringBuilder();
        for (int i = 1, len = queries.size(); i < len; i++) {
            select = queries.get(i);
            alias = aliasGenerator.nextAlias();
            SqlUtils.addSelectFields(query, select.getOutputFields(), alias);
            appendCrossJoin(sb, query, select.getSql(), alias);
        }

        SqlSelect selectQuery = query.toSelect();

        String crossJoins = sb.toString();
        String selectQuerySql = selectQuery.getSql();

        String result =
            new StringBuilder(selectQuerySql.length() + crossJoins.length())
            .append(selectQuerySql)
            .append(crossJoins)
            .toString();

        return StringSelect.of(result)
            .returning(selectQuery.getOutputFields())
            .build();
    }

    private void appendCrossJoin(StringBuilder sb, SqlQuery query, String sql, String tableAlias) {
        sb.append(Util.nl).append("cross join (");

        if (query.isGenerateFormattedSql()) {
            sb.append(Util.nl).append(sql).append(Util.nl).append(')');
        } else {
            sb.append(sql).append(')');
        }

        if (dialect.allowsAs()) {
            sb.append(" as");
        }
        sb.append(' ').append(dialect.quoteIdentifier(tableAlias));
    }
}
