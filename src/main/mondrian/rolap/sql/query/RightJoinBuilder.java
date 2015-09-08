package mondrian.rolap.sql.query;

import mondrian.olap.Util;
import mondrian.rolap.sql.SqlQuery;
import mondrian.spi.Dialect;
import java.util.List;

/**
 * @author Andrey Khayrutdinov
 */
public class RightJoinBuilder {

    public static final String BASE_QUERY_ALIAS = "subsel";
    public static final String JOIN_QUERY_ALIAS = "jndq";

    public static RightJoinBuilder builder(Dialect dialect) {
        return new RightJoinBuilder(dialect);
    }

    private final Dialect dialect;
    private SqlSelect baseQuery;
    private SqlSelect joinQuery;

    public RightJoinBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public RightJoinBuilder query(SqlSelect baseQuery) {
        this.baseQuery = baseQuery;
        return this;
    }

    public RightJoinBuilder rightJoin(SqlSelect joinQuery) {
        this.joinQuery = joinQuery;
        return this;
    }

    public SqlSelect build(Populator populator, Mapper mapper) {
        SqlQueryBuilder queryBuilder = new SqlQueryBuilder(dialect);
        populator.populateOutput(queryBuilder, baseQuery, joinQuery);

        queryBuilder.addFromQuery(baseQuery.getSql(), BASE_QUERY_ALIAS, true);

        SqlSelect select = queryBuilder.toSelect();

        String selectSql = select.getSql();
        String joinQuerySql = joinQuery.getSql();

        StringBuilder sb = new StringBuilder(selectSql.length() * 2 + joinQuerySql.length());
        sb.append(selectSql);
        sb.append(Util.nl).append("right join (").append(joinQuerySql).append(") ");
        if (dialect.allowsAs()) {
            sb.append("as ");
        }
        sb.append(dialect.quoteIdentifier(JOIN_QUERY_ALIAS));
        sb.append(" on ").append(Util.nl).append(mapper.map(baseQuery.getOutputFields(), joinQuery.getOutputFields(), dialect));

        // todo technical debt here
        List<OrderElement> orderByStatements = null;

        if (!baseQuery.getOrderElements().isEmpty()) {
            List<SelectElement> baseQueryOutputFields = baseQuery.getOutputFields();
            int joinQueryOutputSize = joinQuery.getOutputFields().size();

            SqlQuery sqlGenerator = new SqlQuery(dialect, false);
            for (OrderElement element : baseQuery.getOrderElements()) {
                String table;
                int index = findExpression(baseQueryOutputFields, element.getExpression());
                if (index != -1 && index < joinQueryOutputSize) {
                    table = JOIN_QUERY_ALIAS;
                } else {
                    table = BASE_QUERY_ALIAS;
                }
                String quoted = dialect.quoteIdentifier(table, element.getAlias());
                sqlGenerator.addOrderBy(quoted, element.isAscending(), false, element.isNullable());
            }

            String generated = sqlGenerator.toString();
            int index = generated.indexOf("order by");
            if (index != -1) {
                sb.append(Util.nl).append(generated.substring(index));
            }
        }

        return StringSelect.of(sb.toString())
                .returning(select.getOutputFields())
                .orderedBy(orderByStatements)
                .build();
    }

    private int findExpression(List<SelectElement> elements, String expression) {
        for (int i = 0; i < elements.size(); i++) {
            SelectElement element = elements.get(i);
            if (expression.equals(element.getExpression()) || expression.equals(element.getAlias())) {
                return i;
            }
        }
        return -1;
    }

    public interface Populator {
        void populateOutput(SqlQuery query, SqlSelect baseQuery, SqlSelect joinQuery);
    }

    public interface Mapper {
        String map(List<SelectElement> baseQueryOutput, List<SelectElement> joinQueryOutput, Dialect dialect);
    }
}
