package com.isf.uitests.support;

/**
 * Read-only reference values (fictional people). Only unique identifiers (license, VIN, plate, claim number) may be
 * matched exactly; names are used as partial search text because the data set can grow.
 */
public final class SeedData {

    private SeedData() {
    }

    public static final int SMALL_PAGE_SIZE = 5;

    public static final String MARIA = "Maria Gonzalez";
    public static final String MARIA_FRAGMENT = "gONz";
    public static final String JOHN_LICENSE = "D1000002";
    public static final String NOBODY = "Zzyzx Nonexistent";

    // Demo VIN, plate and claim number of the first claim (fictional TST prefix).
    public static final String CLAIM_1 = "CLM-2026-0001";
    // Claim from the bulk seed (V1002); its number is unique.
    public static final String CLAIM_777 = "CLM-2026-0777";
    public static final String VIN_OF_CLAIM_1 = "TSTVIN00000000001";
    public static final String PLATE_OF_CLAIM_1 = "TST-0001";

    // Dates of the first claim, and a period that does not contain them.
    public static final String LOSS_DATE_CLAIM_1 = "2026-01-12";
    public static final String REPORTED_DATE_CLAIM_1 = "2026-01-13";
    public static final String PERIOD_WITHOUT_CLAIM_1_FROM = "2026-03-01";
    public static final String PERIOD_WITHOUT_CLAIM_1_TO = "2026-03-31";

    public static final String OTHER_DRIVER_OF_CLAIM_1 = "John Smith";
    // Bulk seed (V1002) claim that has no other drivers.
    public static final String CLAIM_WITHOUT_OTHER_DRIVERS = "CLM-2026-0291";

    public static final long UNKNOWN_CLAIM_ID = 999_999_999L;
    public static final long CLAIM_1_ID = 1L;
}
