package com.isf.uitests.tests.otherPatterns.component;

import static com.isf.uitests.support.TestGroups.REGRESSION;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PATTERN: component (page fragment) objects. The page object still owns the form and navigation;
 * the table is its own object, reusable and testable on its own.
 */
class ResultsTableComponentTest extends BaseUiTest {

    @Test
    @Tag(REGRESSION)
    void tableListsTheClaimThatWasSearched() {
        searchPage().searchFor(SeedData.CLAIM_1);
        ResultsTable table = new ResultsTable(page);

        assertThat(table.rowOf(SeedData.CLAIM_1)).isVisible();
        assertThat(table.rows()).hasCount(1);
    }

    @Test
    @Tag(REGRESSION)
    void columnsOfEveryRowMentionTheDriver() {
        searchPage().searchFor(SeedData.MARIA);
        ResultsTable table = new ResultsTable(page);

        assertThat(table.rows().first()).isVisible();
        assertThat(table.header("claim")).isVisible();
    }
}
