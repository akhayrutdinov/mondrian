package mondrian.rolap.sql.query;

import mondrian.rolap.SqlStatement;

import java.util.List;

/**
 * @author Andrey Khayrutdinov
 */
// todo Khayrutdinov : java docs
public interface SqlSelect {

    List<SelectElement> getOutputFields();
    List<SqlStatement.Type> getOutputTypes();

    List<OrderElement> getOrderElements();

    String getSql();
}
