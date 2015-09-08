package mondrian.rolap.sql.query;

/**
 * @author Andrey Khayrutdinov
 */
// todo Khayrutdinov: docs
class AliasGenerator {
    private final StringBuilder sb;
    private final int seedLength;
    private int counter;

    AliasGenerator(String seed) {
        this.seedLength = seed.length();
        this.counter = 0;

        sb = new StringBuilder(seedLength + 2);
        sb.append(seed);
    }

    String nextAlias() {
        sb.setLength(seedLength);
        return sb.append(counter++).toString();
    }
}
