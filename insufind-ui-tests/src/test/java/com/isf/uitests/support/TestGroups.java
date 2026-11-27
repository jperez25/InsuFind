package com.isf.uitests.support;

/** JUnit tag names. Constants (not an enum) because annotation values must be compile-time constants. */
public final class TestGroups {

    private TestGroups() {
    }

    /** App is up, main search path works. */
    public static final String SMOKE = "smoke";
    /** Everything else. */
    public static final String REGRESSION = "regression";
}
