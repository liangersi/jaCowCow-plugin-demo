package com.thoughtworks.prepare;

public enum TestValue {

    COVEREDCOUNT,
    COVEREDRATIO,
    MISSEDCOUNT,
    MISSEDRATIO,
    TOTALCOUNT;


    public static Double getValue(TestValue testValue, Double coveredCount, Double missedCount) {
        if (coveredCount + missedCount == 0) {
            return (double) 0;
        }

        switch (testValue) {
            case COVEREDCOUNT:
                return coveredCount;
            case COVEREDRATIO:
                return coveredCount / (coveredCount + missedCount);
            case MISSEDCOUNT:
                return missedCount;
            case MISSEDRATIO:
                return missedCount / (coveredCount + missedCount);
            case TOTALCOUNT:
                return coveredCount + missedCount;
            default:
                return null;
        }
    }
}
