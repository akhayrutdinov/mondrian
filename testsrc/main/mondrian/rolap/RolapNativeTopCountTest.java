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

import mondrian.test.TestContext;

import static mondrian.rolap.RolapNativeTopCountTestCases.*;

/**
 * @author Andrey Khayrutdinov
 * @see RolapNativeTopCountTestCases
 */

// todo Khayrutdinov : AccessControlTest
public class RolapNativeTopCountTest extends BatchTestCase {

    public void setUp() throws Exception {
        super.setUp();
        propSaver.set(propSaver.properties.EnableNativeTopCount, true);
    }

    public void testTopCount_ImplicitCountMeasure() throws Exception {
        assertQueryReturns(IMPLICIT_COUNT_MEASURE_QUERY, IMPLICIT_COUNT_MEASURE_RESULT);
    }

    public void testTopCount_CountMeasure() throws Exception {
        final String schema = TestContext.instance()
            .getSchema(null, CUSTOM_COUNT_MEASURE_CUBE, null, null, null, null);

        TestContext ctx = TestContext.instance()
            .withSchema(schema)
            .withCube(CUSTOM_COUNT_MEASURE_CUBE_NAME);

        ctx.assertQueryReturns(CUSTOM_COUNT_MEASURE_QUERY, CUSTOM_COUNT_MEASURE_RESULT);
    }

    public void testTopCount_SumMeasure() throws Exception {
        assertQueryReturns(SUM_MEASURE_QUERY, SUM_MEASURE_RESULT);
    }


    public void testEmptyCellsAreShown_Countries() {
        assertQueryReturns(EMPTY_CELLS_ARE_SHOWN_COUNTRIES_QUERY, EMPTY_CELLS_ARE_SHOWN_COUNTRIES_RESULT);
    }

    public void testEmptyCellsAreShown_States() {
        assertQueryReturns(EMPTY_CELLS_ARE_SHOWN_STATES_QUERY, EMPTY_CELLS_ARE_SHOWN_STATES_RESULT);
    }

    public void testEmptyCellsAreShown_ButNoMoreThanReallyExist() {
        assertQueryReturns(EMPTY_CELLS_ARE_SHOWN_NOT_MORE_THAN_EXIST_QUERY, EMPTY_CELLS_ARE_SHOWN_NOT_MORE_THAN_EXIST_RESULT);
    }

    public void testEmptyCellsAreHidden_WhenNonEmptyIsDeclaredExplicitly() {
        assertQueryReturns(EMPTY_CELLS_ARE_HIDDEN_WHEN_NON_EMPTY_QUERY, EMPTY_CELLS_ARE_HIDDEN_WHEN_NON_EMPTY_RESULT);
    }


    public void testMimicsHeadWhenTwoParams_States() {
        assertQueryReturns(TOPCOUNT_MIMICS_HEAD_WHEN_TWO_PARAMS_STATES_QUERY, TOPCOUNT_MIMICS_HEAD_WHEN_TWO_PARAMS_STATES_RESULT);
    }

    public void testMimicsHeadWhenTwoParams_Cities() {
        assertQueryReturns(TOPCOUNT_MIMICS_HEAD_WHEN_TWO_PARAMS_CITIES_QUERY, TOPCOUNT_MIMICS_HEAD_WHEN_TWO_PARAMS_CITIES_RESULT);
    }

    public void testMimicsHeadWhenTwoParams_ShowsNotMoreThanExist() {
        assertQueryReturns(RESULTS_ARE_SHOWN_NOT_MORE_THAN_EXIST_TWO_PARAMS_QUERY, RESULTS_ARE_SHOWN_NOT_MORE_THAN_EXIST_TWO_PARAMS_RESULT);
    }

    public void testNonEmptyIsIgnored_WhenTwoParams() {
        assertQueryReturns(NON_EMPTY_IS_IGNORED_WHEN_TWO_PARAMS_QUERY, NON_EMPTY_IS_IGNORED_WHEN_TWO_PARAMS_RESULT);
    }
}

// End RolapNativeTopCountTest.java
