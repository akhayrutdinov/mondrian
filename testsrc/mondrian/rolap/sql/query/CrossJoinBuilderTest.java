package mondrian.rolap.sql.query;

import mondrian.rolap.SqlStatement;
import mondrian.spi.Dialect;
import mondrian.spi.DialectManager;
import mondrian.test.FoodMartTestCase;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static mondrian.rolap.SqlStatement.Type.DOUBLE;
import static mondrian.rolap.SqlStatement.Type.INT;

/**
 * @author Andrey Khayrutdinov
 */
// todo Khayrutdinov: Main.java
public class CrossJoinBuilderTest extends FoodMartTestCase {

    private CrossJoinBuilder builder;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        DataSource ds = getTestContext().getConnection().getDataSource();
        Dialect dialect = DialectManager.createDialect(ds, null);
        builder = CrossJoinBuilder.builder(dialect);
    }

    public void testNoQueries() {
        assertNull(builder.build());
    }

    private StringSelect select(String sql, List<String> fields, List<SqlStatement.Type> types) {
        List<SelectElement> elements = new ArrayList<SelectElement>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            elements.add(new SelectElement(null, field, types.get(i)));
        }
        return StringSelect.of(sql).returning(elements).build();
    }

    public void testOneQuery() {
        StringSelect select = select(
                "select `col` from `tbl`",
                singletonList("`col`"), singletonList(INT));
        SqlSelect crossJoin = builder.append(select).build();
        assertQuery(crossJoin, select.getSql(), singletonList("`col`"), singletonList(INT));
    }

    public void testTwoQueries() {
        StringSelect subQuery1 = select(
                "select "
                        + "`a`.`a` as `c0`, "
                        + "`a`.`b` as `c1` "
                        + "from `a`",
                asList("`c0`", "`c1`"),
                asList(INT, null));
        StringSelect subQuery2 = select(
                "select "
                        + "`b`.`a` as `c0`, "
                        + "`b`.`b` as `c1` "
                        + "from `b`",
                asList("`c0`", "`c1`"),
                asList(DOUBLE, null));

        SqlSelect crossJoin = builder
                .append(subQuery1)
                .append(subQuery2)
                .build();

        String expectedSql = ""
                + "select "
                + "`t0`.`c0` as `c0`, " +
                "`t0`.`c1` as `c1`, " +
                "`t1`.`c0` as `c2`, " +
                "`t1`.`c1` as `c3` " +
                "from (" + subQuery1.getSql() + ") as `t0` " +
                "cross join (" + subQuery2.getSql() + ") as `t1`";

        assertQuery(crossJoin, expectedSql, asList("`c0`", "`c1`", "`c2`", "`c3`"), asList(INT, null, DOUBLE, null));
    }

    public void testThreeQueries() {
        List<SqlStatement.Type> nullTypes = singletonList(null);

        String sql1 = "select a from a";
        SqlSelect select1 = select(sql1, singletonList("a"), nullTypes);

        String sql2 = "select b from b";
        SqlSelect select2 = select(sql2, singletonList("b"), nullTypes);

        String sql3 = "select c from c";
        SqlSelect select3 = select(sql3, singletonList("c"), nullTypes);

        SqlSelect crossJoin = builder
                .append(select1)
                .append(select2)
                .append(select3)
                .build();

        String expectedSql = "" +
                "select " +
                "`t0`.`a` as `c0`, " +
                "`t1`.`b` as `c1`, " +
                "`t2`.`c` as `c2` " +
                "from (" + sql1 + ") as `t0` " +
                "cross join (" + sql2 + ") as `t1` " +
                "cross join (" + sql3 + ") as `t2`";

        assertQuery(crossJoin, expectedSql, asList("`c0`", "`c1`", "`c2`"), Arrays.<SqlStatement.Type>asList(null, null, null) );
    }



    private void assertQuery(SqlSelect crossJoin, String expectedSql, List<String> expectedOutput, List<SqlStatement.Type> types) {
        String crossJoinSql = stripWhiteSpace(crossJoin.getSql());
        expectedSql = stripWhiteSpace(expectedSql);
        assertEquals(expectedSql, crossJoinSql);

        assertListsAreEqual(expectedOutput, extractElementsAliases(crossJoin.getOutputFields()));

        assertListsAreEqual(types, crossJoin.getOutputTypes());
    }

    private List<String> extractElementsAliases(List<SelectElement> elements) {
        List<String> result = new ArrayList<String>(elements.size());
        for (SelectElement element : elements) {
            result.add(element.getAlias());
        }
        return result;
    }

    private String stripWhiteSpace(String string) {
        string = string.replaceAll("\n", " ").replaceAll("\r", " ");
        while (string.contains("  ")) {
            string = string.replaceAll("  ", " ");
        }
        return string;
    }

    private <T> void assertListsAreEqual(List<? extends T> expected, List<? extends T> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            T e = expected.get(i);
            T a = actual.get(i);
            assertEquals(Integer.toString(i), e, a);
        }
    }
}