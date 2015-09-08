package mondrian.rolap.sql.query;

import mondrian.rolap.SqlStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * @author Andrey Khayrutdinov
 */
public class StringSelect implements SqlSelect {

    private final String sql;
    private final List<SelectElement> outputFields;
    private final List<OrderElement> orderByElements;

    StringSelect(String sql, List<SelectElement> outputFields, List<OrderElement> orderByElements) {
        this.sql = sql;
        this.outputFields = outputFields;
        this.orderByElements = orderByElements;
    }

    @Override
    public List<SelectElement> getOutputFields() {
        return outputFields;
    }

    @Override
    public List<SqlStatement.Type> getOutputTypes() {
        List<SqlStatement.Type> types = new ArrayList<SqlStatement.Type>(outputFields.size());
        for (SelectElement field : outputFields) {
            types.add(field.getType());
        }
        return types;
    }

    @Override
    public List<OrderElement> getOrderElements() {
        return orderByElements;
    }

    @Override
    public String getSql() {
        return sql;
    }


    public static Builder of(String sql) {
        return new Builder(sql);
    }

    public static class Builder {
        private String sql;
        private List<SelectElement> outputFields;
        private List<OrderElement> orderElements;

        public Builder(String sql) {
            this.sql = sql;
        }

        public Builder returning(List<SelectElement> fields) {
            this.outputFields = fields;
            return this;
        }

        public Builder orderedBy(List<OrderElement> orderByStatements) {
            this.orderElements = orderByStatements;
            return this;
        }

        public StringSelect build() {
            return new StringSelect(sql, protectList(outputFields), protectList(orderElements));
        }

        private <T> List<T> protectList(List<T> list) {
            if (list == null || list.isEmpty()) {
                return Collections.emptyList();
            } else {
                return unmodifiableList(new ArrayList<T>(list));
            }
        }
    }
}
