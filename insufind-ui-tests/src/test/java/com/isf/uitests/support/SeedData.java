package com.isf.uitests.support;

/** Read-only reference data loaded by the backend's Flyway seed (fictional people). */
public final class SeedData {

    private SeedData() {
    }

    public static final int TOTAL_CLAIMS = 12;
    public static final int DEFAULT_PAGE_SIZE = 5;

    public static final String MARIA = "Maria Gonzalez";
    public static final String JOHN = "John Smith";
    public static final String JOHN_LICENSE = "D1000002";
    public static final String NOBODY = "Zzyzx Nonexistent";

    // Demo VIN and plate of CLM-2026-0001 (fictional TST prefix).
    public static final String VIN_OF_CLAIM_1 = "TSTVIN00000000001";
    public static final String PLATE_OF_CLAIM_1 = "TST-0001";

    // Claims where Maria is the insured driver.
    public static final String[] MARIA_INSURED_CLAIMS = {"CLM-2026-0010", "CLM-2026-0002", "CLM-2026-0001"};
    // Claims where Maria is one of the other drivers.
    public static final String[] MARIA_OTHER_CLAIMS = {"CLM-2026-0011", "CLM-2026-0005"};
    // John: insured on 3 and 7, other driver on 1 and 6 (newest incident first).
    public static final String[] JOHN_ANY_CLAIMS = {"CLM-2026-0007", "CLM-2026-0006", "CLM-2026-0003", "CLM-2026-0001"};
}
