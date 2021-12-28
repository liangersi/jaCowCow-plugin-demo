package com.thoughtworks.prepare;

import java.util.HashMap;
import java.util.Map;

public enum TestReportHeader {

    GROUP,
    PACKAGE,
    CLASS,
    INSTRUCTION_MISSED,
    INSTRUCTION_COVERED,
    BRANCH_MISSED,
    BRANCH_COVERED,
    LINE_MISSED,
    LINE_COVERED,
    COMPLEXITY_MISSED,
    COMPLEXITY_COVERED,
    METHOD_MISSED,
    METHOD_COVERED;

    private static final Map<TestCounter, TestReportHeader[]> counterHeaderMap = new HashMap<>();

    static {
        counterHeaderMap.put(TestCounter.INSTRUCTION, new TestReportHeader[]{INSTRUCTION_COVERED, INSTRUCTION_MISSED});
        counterHeaderMap.put(TestCounter.BRANCH, new TestReportHeader[]{BRANCH_COVERED, BRANCH_MISSED});
        counterHeaderMap.put(TestCounter.LINE, new TestReportHeader[]{LINE_COVERED, LINE_MISSED});
        counterHeaderMap.put(TestCounter.COMPLEXITY, new TestReportHeader[]{COMPLEXITY_COVERED, COMPLEXITY_MISSED});
        counterHeaderMap.put(TestCounter.METHOD, new TestReportHeader[]{METHOD_COVERED, METHOD_MISSED});
    }

    public static TestReportHeader[] getHeadersByCounter(TestCounter testCounter) {
        return counterHeaderMap.get(testCounter);
    }
}
