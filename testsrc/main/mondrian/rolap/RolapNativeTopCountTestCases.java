package mondrian.rolap;

/**
 * @author Andrey Khayrutdinov
 */
class RolapNativeTopCountTestCases {

    // todo Khayrutdinov: description
    static final String IMPLICIT_COUNT_MEASURE_QUERY = ""
            + "SELECT [Measures].[Fact Count] ON COLUMNS, "
            + "TOPCOUNT([Store Type].[All Store Types].Children, 3, [Measures].[Fact Count]) ON ROWS "
            + "FROM [Store]";

    static final String IMPLICIT_COUNT_MEASURE_RESULT = ""
            + "Axis #0:\n"
            + "{}\n"
            + "Axis #1:\n"
            + "{[Measures].[Fact Count]}\n"
            + "Axis #2:\n"
            + "{[Store Type].[Supermarket]}\n"
            + "{[Store Type].[Deluxe Supermarket]}\n"
            + "{[Store Type].[Mid-Size Grocery]}\n"
            + "Row #0: 8\n"
            + "Row #1: 6\n"
            + "Row #2: 4\n";

    // ------------------------------------------------------------------------

    // todo Khayrutdinov: description
    static final String CUSTOM_COUNT_MEASURE_CUBE_NAME = "StoreWithCountM";

    static final String CUSTOM_COUNT_MEASURE_CUBE = ""
            + "  <Cube name=\"StoreWithCountM\" visible=\"true\" cache=\"true\" enabled=\"true\">\n"
            + "    <Table name=\"store\">\n"
            + "    </Table>\n"
            + "    <Dimension visible=\"true\" highCardinality=\"false\" name=\"Store Type\">\n"
            + "      <Hierarchy visible=\"true\" hasAll=\"true\">\n"
            + "        <Level name=\"Store Type\" visible=\"true\" column=\"store_type\" type=\"String\" uniqueMembers=\"true\" levelType=\"Regular\" hideMemberIf=\"Never\">\n"
            + "        </Level>\n"
            + "      </Hierarchy>\n"
            + "    </Dimension>\n"
            + "    <DimensionUsage source=\"Store\" name=\"Store\" visible=\"true\" highCardinality=\"false\">\n"
            + "    </DimensionUsage>\n"
            + "    <Dimension visible=\"true\" highCardinality=\"false\" name=\"Has coffee bar\">\n"
            + "      <Hierarchy visible=\"true\" hasAll=\"true\">\n"
            + "        <Level name=\"Has coffee bar\" visible=\"true\" column=\"coffee_bar\" type=\"Boolean\" uniqueMembers=\"true\" levelType=\"Regular\" hideMemberIf=\"Never\">\n"
            + "        </Level>\n"
            + "      </Hierarchy>\n"
            + "    </Dimension>\n"
            + "    <Measure name=\"Store Sqft\" column=\"store_sqft\" formatString=\"#,###\" aggregator=\"sum\">\n"
            + "    </Measure>\n"
            + "    <Measure name=\"Grocery Sqft\" column=\"grocery_sqft\" formatString=\"#,###\" aggregator=\"sum\">\n"
            + "    </Measure>\n"
            + "    <Measure name=\"CountM\" column=\"store_id\" formatString=\"Standard\" aggregator=\"count\" visible=\"true\">\n"
            + "    </Measure>\n"
            + "  </Cube>";

    static final String CUSTOM_COUNT_MEASURE_QUERY = ""
            + "SELECT [Measures].[CountM] ON COLUMNS, "
            + "TOPCOUNT([Store Type].[All Store Types].Children, 3, [Measures].[CountM]) ON ROWS "
            + "FROM [StoreWithCountM]";

    static final String CUSTOM_COUNT_MEASURE_RESULT = ""
            + "Axis #0:\n"
            + "{}\n"
            + "Axis #1:\n"
            + "{[Measures].[CountM]}\n"
            + "Axis #2:\n"
            + "{[Store Type].[Supermarket]}\n"
            + "{[Store Type].[Deluxe Supermarket]}\n"
            + "{[Store Type].[Mid-Size Grocery]}\n"
            + "Row #0: 8\n"
            + "Row #1: 6\n"
            + "Row #2: 4\n";

    // ------------------------------------------------------------------------

    // todo Khayrutdinov: description
    static final String SUM_MEASURE_QUERY = ""
            + "SELECT [Measures].[Store Sqft] ON COLUMNS, "
            + "TOPCOUNT([Store Type].[All Store Types].Children, 3, [Measures].[Store Sqft]) ON ROWS "
            + "FROM [Store]";

    static final String SUM_MEASURE_RESULT = ""
            + "Axis #0:\n"
            + "{}\n"
            + "Axis #1:\n"
            + "{[Measures].[Store Sqft]}\n"
            + "Axis #2:\n"
            + "{[Store Type].[Supermarket]}\n"
            + "{[Store Type].[Deluxe Supermarket]}\n"
            + "{[Store Type].[Mid-Size Grocery]}\n"
            + "Row #0: 193,480\n"
            + "Row #1: 146,045\n"
            + "Row #2: 109,343\n";

    // ------------------------------------------------------------------------

    // todo Khayrutdinov: description
    static final String EMPTY_CELLS_ARE_SHOWN_COUNTRIES_QUERY = ""
            + "SELECT [Measures].[Unit Sales] ON COLUMNS, "
            + "TOPCOUNT([Customers].[Country].Members, 2, [Measures].[Unit Sales]) ON ROWS "
            + "FROM [Sales] "
            + "WHERE [Time].[1997].[Q3]";

    static final String EMPTY_CELLS_ARE_SHOWN_COUNTRIES_RESULT = ""
            + "Axis #0:\n"
            + "{[Time].[1997].[Q3]}\n"
            + "Axis #1:\n"
            + "{[Measures].[Unit Sales]}\n"
            + "Axis #2:\n"
            + "{[Customers].[USA]}\n"
            + "{[Customers].[Canada]}\n"
            + "Row #0: 65,848\n"
            + "Row #1: \n";

    // ------------------------------------------------------------------------

    // todo Khayrutdinov: description
    static final String EMPTY_CELLS_ARE_SHOWN_STATES_QUERY = ""
            + "SELECT [Measures].[Unit Sales] ON COLUMNS, "
            + "TOPCOUNT([Customers].[State Province].Members, 6, [Measures].[Unit Sales]) ON ROWS "
            + "FROM [Sales] "
            + "WHERE [Time].[1997].[Q3]";

    static final String EMPTY_CELLS_ARE_SHOWN_STATES_RESULT = ""
            + "Axis #0:\n" +
            "{[Time].[1997].[Q3]}\n" +
            "Axis #1:\n" +
            "{[Measures].[Unit Sales]}\n" +
            "Axis #2:\n" +
            "{[Customers].[USA].[WA]}\n" +
            "{[Customers].[USA].[CA]}\n" +
            "{[Customers].[USA].[OR]}\n" +
            "{[Customers].[Canada].[BC]}\n" +
            "{[Customers].[Mexico].[DF]}\n" +
            "{[Customers].[Mexico].[Guerrero]}\n" +
            "Row #0: 30,538\n" +
            "Row #1: 18,370\n" +
            "Row #2: 16,940\n" +
            "Row #3: \n" +
            "Row #4: \n" +
            "Row #5: \n";

        // ------------------------------------------------------------------------

        // todo Khayrutdinov: description
        static final String EMPTY_CELLS_ARE_SHOWN_NOT_MORE_THAN_EXIST_QUERY = ""
                + "SELECT [Measures].[Unit Sales] ON COLUMNS, "
                + "TOPCOUNT([Customers].[Country].Members, 10, [Measures].[Unit Sales]) ON ROWS "
                + "FROM [Sales] "
                + "WHERE [Time].[1997].[Q3]";

        static final String EMPTY_CELLS_ARE_SHOWN_NOT_MORE_THAN_EXIST_RESULT = ""
                + "Axis #0:\n"
                + "{[Time].[1997].[Q3]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Unit Sales]}\n"
                + "Axis #2:\n"
                + "{[Customers].[USA]}\n"
                + "{[Customers].[Canada]}\n"
                + "{[Customers].[Mexico]}\n"
                + "Row #0: 65,848\n"
                + "Row #1: \n"
                + "Row #2: \n";

    //  -----------------------------------------------------------------------

    // todo Khayrutdinov: description
    static final String EMPTY_CELLS_ARE_HIDDEN_WHEN_NON_EMPTY_QUERY = ""
            + "SELECT [Measures].[Unit Sales] ON COLUMNS, "
            + "NON EMPTY TOPCOUNT([Customers].[Country].Members, 2, [Measures].[Unit Sales]) ON ROWS "
            + "FROM [Sales] "
            + "WHERE [Time].[1997].[Q3]";

    static final String EMPTY_CELLS_ARE_HIDDEN_WHEN_NON_EMPTY_RESULT = ""
            + "Axis #0:\n"
            + "{[Time].[1997].[Q3]}\n"
            + "Axis #1:\n"
            + "{[Measures].[Unit Sales]}\n"
            + "Axis #2:\n"
            + "{[Customers].[USA]}\n"
            + "Row #0: 65,848\n";

    // -----------------------------------------------------------------------

    // todo Khayrutdinov: description
    static final String TOPCOUNT_MIMICS_HEAD_WHEN_TWO_PARAMS_STATES_QUERY = ""
                + "SELECT TOPCOUNT([Customers].[State Province].members, 3) ON COLUMNS "
                + "FROM [Sales] ";

    static final String TOPCOUNT_MIMICS_HEAD_WHEN_TWO_PARAMS_STATES_RESULT = ""
                + "Axis #0:\n" +
            "{}\n" +
            "Axis #1:\n" +
            "{[Customers].[Canada].[BC]}\n" +
            "{[Customers].[Mexico].[DF]}\n" +
            "{[Customers].[Mexico].[Guerrero]}\n" +
            "Row #0: \n" +
            "Row #0: \n" +
            "Row #0: \n";

    // -----------------------------------------------------------------------

        // todo Khayrutdinov: description
        static final String TOPCOUNT_MIMICS_HEAD_WHEN_TWO_PARAMS_CITIES_QUERY = ""
                + "SELECT TOPCOUNT([Customers].[City].members, 30) ON COLUMNS "
                + "FROM [Sales] ";

        static final String TOPCOUNT_MIMICS_HEAD_WHEN_TWO_PARAMS_CITIES_RESULT = ""
                + "Axis #0:\n" +
                "{}\n" +
                "Axis #1:\n" +
                "{[Customers].[Canada].[BC].[Burnaby]}\n" +
                "{[Customers].[Canada].[BC].[Cliffside]}\n" +
                "{[Customers].[Canada].[BC].[Haney]}\n" +
                "{[Customers].[Canada].[BC].[Ladner]}\n" +
                "{[Customers].[Canada].[BC].[Langford]}\n" +
                "{[Customers].[Canada].[BC].[Langley]}\n" +
                "{[Customers].[Canada].[BC].[Metchosin]}\n" +
                "{[Customers].[Canada].[BC].[N. Vancouver]}\n" +
                "{[Customers].[Canada].[BC].[Newton]}\n" +
                "{[Customers].[Canada].[BC].[Oak Bay]}\n" +
                "{[Customers].[Canada].[BC].[Port Hammond]}\n" +
                "{[Customers].[Canada].[BC].[Richmond]}\n" +
                "{[Customers].[Canada].[BC].[Royal Oak]}\n" +
                "{[Customers].[Canada].[BC].[Shawnee]}\n" +
                "{[Customers].[Canada].[BC].[Sooke]}\n" +
                "{[Customers].[Canada].[BC].[Vancouver]}\n" +
                "{[Customers].[Canada].[BC].[Victoria]}\n" +
                "{[Customers].[Canada].[BC].[Westminster]}\n" +
                "{[Customers].[Mexico].[DF].[San Andres]}\n" +
                "{[Customers].[Mexico].[DF].[Santa Anita]}\n" +
                "{[Customers].[Mexico].[DF].[Santa Fe]}\n" +
                "{[Customers].[Mexico].[DF].[Tixapan]}\n" +
                "{[Customers].[Mexico].[Guerrero].[Acapulco]}\n" +
                "{[Customers].[Mexico].[Jalisco].[Guadalajara]}\n" +
                "{[Customers].[Mexico].[Mexico].[Mexico City]}\n" +
                "{[Customers].[Mexico].[Oaxaca].[Tlaxiaco]}\n" +
                "{[Customers].[Mexico].[Sinaloa].[La Cruz]}\n" +
                "{[Customers].[Mexico].[Veracruz].[Orizaba]}\n" +
                "{[Customers].[Mexico].[Yucatan].[Merida]}\n" +
                "{[Customers].[Mexico].[Zacatecas].[Camacho]}\n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n";

    // -----------------------------------------------------------------------

        // todo Khayrutdinov: description
        static final String RESULTS_ARE_SHOWN_NOT_MORE_THAN_EXIST_TWO_PARAMS_QUERY = ""
                + "SELECT TOPCOUNT([Customers].[Country].members, 5) ON COLUMNS "
                + "FROM [Sales] ";

        static final String RESULTS_ARE_SHOWN_NOT_MORE_THAN_EXIST_TWO_PARAMS_RESULT = ""
                + "Axis #0:\n" +
                "{}\n" +
                "Axis #1:\n" +
                "{[Customers].[Canada]}\n" +
                "{[Customers].[Mexico]}\n" +
                "{[Customers].[USA]}\n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: 266,773\n";

    // -----------------------------------------------------------------------

    // todo Khayrutdinov: description
    static final String NON_EMPTY_IS_IGNORED_WHEN_TWO_PARAMS_QUERY = ""
                + "SELECT NON EMPTY TOPCOUNT([Customers].[State Province].members, 3) ON COLUMNS "
                + "FROM [Sales] ";

    static final String NON_EMPTY_IS_IGNORED_WHEN_TWO_PARAMS_RESULT = ""
                + "Axis #0:\n" +
                "{}\n" +
                "Axis #1:\n" +
                "{[Customers].[Canada].[BC]}\n" +
                "{[Customers].[Mexico].[DF]}\n" +
                "{[Customers].[Mexico].[Guerrero]}\n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #0: \n";
}
