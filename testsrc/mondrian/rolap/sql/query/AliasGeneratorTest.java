package mondrian.rolap.sql.query;

import junit.framework.TestCase;

/**
 * @author Andrey Khayrutdinov
 */
// todo Khayrutdinov: include into Main
public class AliasGeneratorTest extends TestCase {

    public void testNextAlias_OneCharSeed() {
        AliasGenerator generator = new AliasGenerator("a");
        for (int i = 0; i < 10; i++) {
            String expected = "a" + i;
            assertEquals(expected, generator.nextAlias());
        }
    }

    public void testNextAlias_EmptySeed() {
        AliasGenerator generator = new AliasGenerator("");
        for (int i = 0; i < 10; i++) {
            String expected = Integer.toString(i);
            assertEquals(expected, generator.nextAlias());
        }
    }
}