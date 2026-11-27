package com.isf.uitests.tests.otherPatterns.visual;

import static com.isf.uitests.support.TestGroups.REGRESSION;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PATTERN: snapshot testing, ARIA flavor. One assertion checks the structure of a whole region
 * (roles, names, text) instead of many element assertions. It is also an accessibility check.
 * Pixel snapshots (toHaveScreenshot) exist in the TypeScript runner but not in Playwright for Java.
 * Templates match partially, so only list what matters.
 */
class AriaSnapshotTest extends BaseUiTest {

    @Test
    @Tag(REGRESSION)
    void claimDetailExposesItsStructure() {
        claimDetailPage().open(SeedData.CLAIM_1_ID);

        assertThat(page.getByTestId("claim-detail")).matchesAriaSnapshot("""
                - article:
                  - heading "%s" [level=1]
                  - term: Status
                  - definition: Open
                  - term: Type
                  - definition: Collision
                  - term: Insured driver
                  - definition: /Maria Gonzalez .*/
                  - term: Other drivers
                  - definition:
                    - list:
                      - listitem: /John Smith .*/
                """.formatted(SeedData.CLAIM_1));
    }
}
