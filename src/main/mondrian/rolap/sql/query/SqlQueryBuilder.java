package mondrian.rolap.sql.query;

import mondrian.rolap.SqlStatement;
import mondrian.rolap.sql.SqlQuery;
import mondrian.spi.Dialect;
import mondrian.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey Khayrutdinov
 */
public class SqlQueryBuilder extends SqlQuery {

    private final List<SelectElement> outputFields = new ArrayList<SelectElement>();
    private final List<OrderElement> orderByExpr = new ArrayList<OrderElement>();

    public SqlQueryBuilder(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String addSelect(String expression, SqlStatement.Type type, String alias) {
        if (alias == null) {
            outputFields.add(new SelectElement(expression, null, type));
        } else {
            outputFields.add(new SelectElement(expression, dialect.quoteIdentifier(alias), type));
        }
        return super.addSelect(expression, type, alias);
    }

    @Override
    public void addOrderBy(String expr, String alias, boolean ascending, boolean prepend, boolean nullable, boolean collateNullsLast) {
        super.addOrderBy(expr, alias, ascending, prepend, nullable, collateNullsLast);
        String matchingOutputAlias = columnAliases.get(expr);
        if (matchingOutputAlias != null) {
            OrderElement element = new OrderElement(expr, matchingOutputAlias, ascending, nullable);
            if (prepend) {
                orderByExpr.add(0, element);
            } else {
                orderByExpr.add(element);
            }
        }
    }

    public SqlSelect toSelect() {
        Pair<String, List<SqlStatement.Type>> pair = toSqlAndTypes();
        return StringSelect.of(pair.left).returning(outputFields).orderedBy(orderByExpr).build();
    }
}
