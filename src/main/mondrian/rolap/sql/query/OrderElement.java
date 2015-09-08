package mondrian.rolap.sql.query;

/**
 * @author Andrey Khayrutdinov
 */
public class OrderElement {
    private final String expression;
    private final String alias;
    private final boolean ascending;
    private final boolean nullable;

    public OrderElement(String expression, String alias, boolean ascending, boolean nullable) {
        this.expression = expression;
        this.alias = alias;
        this.ascending = ascending;
        this.nullable = nullable;
    }

    public String getExpression() {
        return expression;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isAscending() {
        return ascending;
    }

    public boolean isNullable() {
        return nullable;
    }
}
