/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2015-2015 Pentaho and others
// All Rights Reserved.
*/
package mondrian.rolap;

import mondrian.olap.Result;
import mondrian.test.TestContext;

import static mondrian.rolap.RolapNativeTopCountTestCases.*;

/**
 * @author Andrey Khayrutdinov
 */
public class RolapNativeTopCountVersusNonNativeTest extends BatchTestCase {

    private Result execute(boolean isNative, String query, TestContext ctx) {
        propSaver.set(propSaver.properties.EnableNativeTopCount, isNative);
        propSaver.set(propSaver.properties.EnableNativeCrossJoin, isNative);
        propSaver.set(propSaver.properties.EnableNativeFilter, isNative);
        propSaver.set(propSaver.properties.EnableNativeNonEmpty, isNative);
        return ctx.executeQuery(query);
    }

    private Result executeNative(String query, TestContext ctx) {
        return execute(true, query, ctx);
    }

    private Result executeNonNative(String query, TestContext ctx) {
        return execute(false, query, ctx);
    }

    private void assertResultsAreEqual(String testCase, String query) {
        assertResultsAreEqual(testCase, query, getTestContext());
    }

    private void assertResultsAreEqual(
        String testCase,
        String query,
        TestContext ctx)
    {
        Result nativeResult = executeNative(query, ctx);
        ctx.flushSchemaCache();
        Result nonNativeResult = executeNonNative(query, ctx);
        assertEquals(
            String.format(
                "[%s]: native and non-native results of the query differ. The query:\n\t\t%s",
                testCase,
                query),
            TestContext.toString(nativeResult),
            TestContext.toString(nonNativeResult));
    }


    public void testTopCount_ImplicitCountMeasure() throws Exception {
        assertResultsAreEqual(
            "Implicit Count Measure", IMPLICIT_COUNT_MEASURE_QUERY);
    }

    public void testTopCount_SumMeasure() throws Exception {
        assertResultsAreEqual(
            "Sum Measure", SUM_MEASURE_QUERY);
    }

    public void testTopCount_CountMeasure() throws Exception {
        final String schema = TestContext.instance()
            .getSchema(null, CUSTOM_COUNT_MEASURE_CUBE, null, null, null, null);

        TestContext ctx = TestContext.instance()
            .withSchema(schema)
            .withCube(CUSTOM_COUNT_MEASURE_CUBE_NAME);

        assertResultsAreEqual(
            "Custom Count Measure", CUSTOM_COUNT_MEASURE_QUERY, ctx);
    }


    public void testEmptyCellsAreShown_Countries() throws Exception {
        assertResultsAreEqual(
            "Empty Cells Are Shown - Countries",
            EMPTY_CELLS_ARE_SHOWN_COUNTRIES_QUERY);
    }

    public void testEmptyCellsAreShown_States() throws Exception {
        assertResultsAreEqual(
            "Empty Cells Are Shown - States",
            EMPTY_CELLS_ARE_SHOWN_STATES_QUERY);
    }

    public void testEmptyCellsAreShown_ButNoMoreThanReallyExist() {
        assertResultsAreEqual(
            "Empty Cells Are Shown - But no more than really exist",
            EMPTY_CELLS_ARE_SHOWN_NOT_MORE_THAN_EXIST_QUERY);
    }

    public void testEmptyCellsAreHidden_WhenNonEmptyIsDeclaredExplicitly() {
        assertResultsAreEqual(
            "Empty Cells Are Hidden - When NON EMPTY is declared explicitly",
            EMPTY_CELLS_ARE_HIDDEN_WHEN_NON_EMPTY_QUERY);
    }
}

// End RolapNativeTopCountVersusNonNativeTest.java