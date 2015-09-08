package mondrian.rolap.sql.query;

import mondrian.rolap.SqlStatement;

/**
 * @author Andrey Khayrutdinov
 */
public class SelectElement {
    private final String expression;
    private final String alias;
    private final SqlStatement.Type type;

    public SelectElement(String expression, String alias, SqlStatement.Type type) {
        this.expression = expression;
        this.alias = (alias == null) ? expression : alias;
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public String getAlias() {
        return alias;
    }

    public SqlStatement.Type getType() {
        return type;
    }
}
