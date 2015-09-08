package mondrian.rolap.sql.query;

import mondrian.rolap.SqlStatement;
import mondrian.rolap.sql.SqlQuery;
import mondrian.spi.Dialect;

import java.util.List;

/**
 * @author Andrey Khayrutdinov
 */
public class SqlUtils {

    public static void addSelectFields(SqlQuery query, List<SelectElement> fields, String tableAlias) {
        StringBuilder sb = new StringBuilder();
        Dialect dialect = query.getDialect();
        String[] pair = new String[]{tableAlias, null};
        for (SelectElement field : fields) {
            sb.setLength(0);
            pair[1] = field.getAlias();
            dialect.quoteIdentifier(sb, pair);
            String expression = sb.toString();

            SqlStatement.Type type = field.getType();
            query.addSelect(expression, type);
        }
    }
}
