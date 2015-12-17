package mondrian.test;

import mondrian.test.loader.MondrianFoodMartLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

/**
 * @author Andrey Khayrutdinov
 */
// todo Xmx2048m!
// todo optimise load - save preloaded statements in memory
public class InMemoryFoodMartTestBase extends PropertyRestoringTestCase {

    private Connection aliveConnection;

    @Override
    public final void setUp() throws Exception {
        super.setUp();

        final String driver = "org.hsqldb.jdbcDriver";
        Class.forName(driver);
        final String url = "jdbc:hsqldb:mem:foodmart" + new Random().nextInt(Short.MAX_VALUE) + ";shutdown=true;default_schema=true";
        aliveConnection = DriverManager.getConnection(url, "sa", "");

        String[] args = new String[] {
            "-aggregates",
            "-tables",
            "-data",
            "-indexes",
            // todo: unpacked in advance!
            "-inputFile=demo/FoodMartCreateData.sql",
            "-jdbcDrivers=" + driver,
            "-outputJdbcURL=" + url,
            "-outputJdbcUser=sa",
            "-outputJdbcPassword="
        };
        MondrianFoodMartLoader.main(args);

        propSaver.set(propSaver.properties.JdbcDrivers, driver);
        propSaver.set(propSaver.properties.FoodmartJdbcURL, url);

        Statement statement = aliveConnection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery("select count(*) from \"sales_fact_1997\"");
            try {
                int records = 0;
                if (resultSet.next()) {
                    records = resultSet.getInt(1);
                }
                if (records != 86837) {
                    throw new IllegalStateException();
                }
            } finally {
                resultSet.close();
            }
        } finally {
            statement.close();
        }

        doSetUp();
    }

    protected void doSetUp() throws Exception {
    }


    @Override
    public final void tearDown() throws Exception {
        try {
            doTearDown();
        } finally {
            try {
                if (context != null) {
                    context.close();
                    context = null;
                }

                aliveConnection.close();
                aliveConnection = null;
                System.gc();
            } finally {
                super.tearDown();
            }
        }
    }

    protected void doTearDown() throws Exception {
    }


    private TestContext context;

    public final TestContext getTestContext() {
        if (context == null) {
            context = TestContext.instance().withFreshConnection();
        }
        return context;
    }

    public void assertQueryReturns(String query, String desiredResult) {
        getTestContext().assertQueryReturns(query, desiredResult);
    }
}
