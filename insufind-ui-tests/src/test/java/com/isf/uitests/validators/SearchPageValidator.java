package com.isf.uitests.validators;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.isf.uitests.pages.SearchPage;
import com.isf.uitests.support.SearchScope;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions.ContainsTextOptions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/** Fluent checks for the search page. Prefers partial, dynamic checks over exact data. */
public class SearchPageValidator extends ValidatorBase<SearchPageValidator, SearchPage> {

    private static final ContainsTextOptions IGNORE_CASE = new ContainsTextOptions().setIgnoreCase(true);

    private SearchPageValidator(SearchPage page) {
        super(page);
    }

    public static SearchPageValidator validate(SearchPage page) {
        return new SearchPageValidator(page);
    }

    // --- states

    public SearchPageValidator showsPlaceholder() {
        assertThat(page.resultsTable()).isVisible();
        assertThat(page.tablePlaceholder()).isVisible();
        assertThat(page.rows()).hasCount(0);
        return this;
    }

    public SearchPageValidator showsLoading() {
        assertThat(page.loading()).isVisible();
        return this;
    }

    public SearchPageValidator showsResults() {
        assertThat(page.rows().first()).isVisible();
        return this;
    }

    public SearchPageValidator showsEmptyState() {
        assertThat(page.emptyState()).isVisible();
        assertThat(page.rows()).hasCount(0);
        return this;
    }

    public SearchPageValidator showsError() {
        assertThat(page.errorMessage()).isVisible();
        assertThat(page.rows()).hasCount(0);
        return this;
    }

    // --- rows

    public SearchPageValidator hasRows(int expected) {
        assertThat(page.rows()).hasCount(expected);
        return this;
    }

    /** Every visible row has the text in the given column (case-insensitive, partial). */
    public SearchPageValidator allCellsContain(String columnKey, String text) {
        showsResults();
        for (Locator cell : page.cells(columnKey).all()) {
            assertThat(cell).containsText(text, IGNORE_CASE);
        }
        return this;
    }

    /** Every visible row shows the text somewhere (case-insensitive, partial). */
    public SearchPageValidator allRowsContain(String text) {
        showsResults();
                for (Locator row : page.rows().all()) {
            assertThat(row).containsText(text, IGNORE_CASE);
        }
        return this;
    }

    /** No row shows the text in the given column. */
    public SearchPageValidator noCellContains(String columnKey, String text) {
        showsResults();
        for (Locator cell : page.cells(columnKey).all()) {
            assertThat(cell).not().containsText(text, IGNORE_CASE);
        }
        return this;
    }

    /** A claim with this (unique) number is listed. */
    public SearchPageValidator listsClaim(String claimNumber) {
        assertThat(page.row(claimNumber)).isVisible();
        return this;
    }

    /** Exactly these claims, in this order. Use only for unique identifiers (VIN, claim number). */
    public SearchPageValidator listsExactly(String... claimNumbers) {
        assertThat(page.claimNumbers()).hasText(claimNumbers);
        return this;
    }

    /** The visible page is ordered by the column (text compare). Retries until the DOM matches. */
    public SearchPageValidator isSortedBy(String columnKey, boolean ascending) {
        showsResults();
        List<String> sorted = new ArrayList<>(page.cells(columnKey).allTextContents());
        sorted.sort(ascending ? Comparator.naturalOrder() : Comparator.reverseOrder());
        assertThat(page.cells(columnKey)).hasText(sorted.toArray(new String[0]));
        return this;
    }

    public SearchPageValidator hasColumn(String columnKey) {
        assertThat(page.header(columnKey)).isVisible();
        return this;
    }

    public SearchPageValidator lacksColumn(String columnKey) {
        assertThat(page.header(columnKey)).hasCount(0);
        return this;
    }

    // --- optional (period) section

    public SearchPageValidator optionalFieldsAreFolded() {
        assertThat(page.optionalToggle()).hasAttribute("aria-expanded", "false");
        assertThat(page.optionalFields()).hasCount(0);
        return this;
    }

    public SearchPageValidator optionalFieldsAreOpen() {
        assertThat(page.optionalToggle()).hasAttribute("aria-expanded", "true");
        assertThat(page.optionalFields()).isVisible();
        assertThat(page.lossFrom()).isVisible();
        assertThat(page.lossTo()).isVisible();
        assertThat(page.reportedFrom()).isVisible();
        assertThat(page.reportedTo()).isVisible();
        return this;
    }

    public SearchPageValidator lossPeriodIs(String from, String to) {
        assertThat(page.lossFrom()).hasValue(from);
        assertThat(page.lossTo()).hasValue(to);
        return this;
    }

    public SearchPageValidator reportedPeriodIs(String from, String to) {
        assertThat(page.reportedFrom()).hasValue(from);
        assertThat(page.reportedTo()).hasValue(to);
        return this;
    }

    public SearchPageValidator hasScope(SearchScope scope) {
        assertThat(page.scopeSelect()).hasValue(scope.optionValue());
        return this;
    }

    // --- summary and paging

    /** The total changed from {@code previous} (retries until it does) and is smaller than it. */
    public SearchPageValidator hasFewerClaimsThan(int previous) {
        assertThat(page.resultsSummary()).not().hasText(previous + " claims");
        assertThat(page.resultsSummary()).hasText(Pattern.compile("\\d+ claims?"));
        org.assertj.core.api.Assertions.assertThat(page.totalClaims()).isLessThan(previous);
        return this;
    }

    /** At most this many rows are shown (the last page or a small result set can have fewer). */
    public SearchPageValidator hasAtMostRows(int max) {
        showsResults();
        org.assertj.core.api.Assertions.assertThat(page.rows().count()).isLessThanOrEqualTo(max);
        return this;
    }


    public SearchPageValidator hasTotalClaims(int expected) {
        assertThat(page.resultsSummary()).hasText(expected + (expected == 1 ? " claim" : " claims"));
        return this;
    }

    public SearchPageValidator isOnPage(int pageNumber) {
        assertThat(page.pageIndicator()).hasText(Pattern.compile("Page " + pageNumber + " of \\d+"));
        return this;
    }

    /** The pager shows ceil(total / pageSize) pages, where total comes from the results summary. */
    public SearchPageValidator pagerMatchesSummaryFor(int pageSize) {
        assertThat(page.resultsSummary()).hasText(Pattern.compile("\\d+ claims?"));
        int total = Integer.parseInt(page.resultsSummary().innerText().replaceAll("\\D", ""));
        int pages = Math.max(1, (total + pageSize - 1) / pageSize);
        assertThat(page.pageIndicator()).hasText(Pattern.compile("Page \\d+ of " + pages));
        return this;
    }

    public SearchPageValidator hasPreviousPageEnabled(boolean enabled) {
        if (enabled) {
            assertThat(page.previousPageButton()).isEnabled();
        } else {
            assertThat(page.previousPageButton()).isDisabled();
        }
        return this;
    }

    public SearchPageValidator hasNextPageEnabled(boolean enabled) {
        if (enabled) {
            assertThat(page.nextPageButton()).isEnabled();
        } else {
            assertThat(page.nextPageButton()).isDisabled();
        }
        return this;
    }

    public SearchPageValidator hasQueryValue(String expected) {
        assertThat(page.queryInput()).hasValue(expected);
        return this;
    }
}
