package com.isf.uitests.pages;

import com.isf.uitests.support.ClaimStatus;
import com.isf.uitests.support.ClaimType;
import com.isf.uitests.support.SearchScope;
import com.isf.uitests.validators.SearchPageValidator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import java.util.regex.Pattern;

/**
 * The claim search page. Acts and reads, never asserts. Every action returns the page the user lands on, so tests
 * chain navigation; {@link #validate()} switches to the fluent checks.
 */
public class SearchPage {

    private final Page page;

    public SearchPage(Page page) {
        this.page = page;
    }

    private static final Pattern SEARCH_API = Pattern.compile(".*/api/claims/search\\?.*");

    public SearchPage open() {
        page.navigate("/");
        return this;
    }

    public SearchPage searchFor(String query) {
        queryInput().fill(query);
        return submit();
    }

    public SearchPage searchFor(String query, SearchScope scope) {
        scopeSelect().selectOption(scope.optionValue());
        return searchFor(query);
    }

    /** Clicks Search and waits until the search is committed to the URL, so later actions do not race it. */
    private SearchPage submit() {
        searchButton().click();
        page.waitForURL(Pattern.compile(".*[?&]tab=.*"));
        return this;
    }

    /** Folds or unfolds the optional (period) section. */
    public SearchPage toggleOptionalFields() {
        optionalToggle().click();
        return this;
    }

    public Locator optionalToggle() {
        return page.getByTestId("optional-toggle");
    }

    public Locator optionalFields() {
        return page.getByTestId("optional-fields");
    }

    public Locator lossFrom() {
        return page.getByTestId("loss-from");
    }

    public Locator lossTo() {
        return page.getByTestId("loss-to");
    }

    public Locator reportedFrom() {
        return page.getByTestId("reported-from");
    }

    public Locator reportedTo() {
        return page.getByTestId("reported-to");
    }

    /** Opens a search directly from a URL query string (the search lives in the URL). */
    public SearchPage openWith(String queryString) {
        page.navigate("/?" + queryString);
        return this;
    }

    public SearchPage reload() {
        page.reload();
        return this;
    }

    public SearchPage clear() {
        page.getByTestId("clear-button").click();
        return this;
    }

    public SearchPage setLossPeriod(String from, String to) {
        lossFrom().fill(from);
        lossTo().fill(to);
        return this;
    }

    public SearchPage setReportedPeriod(String from, String to) {
        reportedFrom().fill(from);
        reportedTo().fill(to);
        return this;
    }

    /** Opens the filter/column panel if it is closed (the button toggles it). */
    public SearchPage openFilterPanel() {
        if (page.getByTestId("filter-panel").count() == 0) {
            page.getByTestId("columns-button").click();
        }
        return this;
    }

    public SearchPage filterByStatus(ClaimStatus status) {
        openFilterPanel();
        page.getByTestId("filter-status-" + status.name()).click();
        return this;
    }

    public SearchPage clearStatusFilter() {
        openFilterPanel();
        page.getByTestId("filter-status-all").click();
        return this;
    }

    public SearchPage filterByType(ClaimType type) {
        openFilterPanel();
        page.getByTestId("filter-type-" + type.name()).click();
        return this;
    }

    public SearchPage clearTypeFilter() {
        openFilterPanel();
        page.getByTestId("filter-type-all").click();
        return this;
    }

    /** Total claims from the results summary, once it is shown. */
    public int totalClaims() {
        resultsSummary().waitFor();
        return Integer.parseInt(resultsSummary().innerText().replaceAll("\\D", ""));
    }

    public SearchPage previousPage() {
        previousPageButton().click();
        return this;
    }

    public SearchPage searchByVin(String vin) {
        page.getByTestId("tab-vehicle").click();
        page.getByTestId("vin-input").fill(vin);
        return submit();
    }

    public SearchPage showColumn(String columnKey) {
        openFilterPanel();
        page.getByTestId("column-toggle-" + columnKey).check();
        return this;
    }

    public SearchPage sortBy(String columnKey) {
        page.getByTestId("sort-" + columnKey).click();
        return this;
    }

    public SearchPageValidator validate() {
        return SearchPageValidator.validate(this);
    }

    /** Makes every search call fail with a 500. Call before searching. */
    public SearchPage failSearchApi() {
        page.route(SEARCH_API, route -> route.fulfill(new Route.FulfillOptions().setStatus(500).setBody("{}")));
        return this;
    }

    public Locator loading() {
        return page.getByTestId("loading");
    }

    public Locator resultsTable() {
        return page.getByTestId("results-table");
    }

    public Locator tablePlaceholder() {
        return page.getByTestId("table-placeholder");
    }

    public Locator header(String columnKey) {
        return page.getByTestId("header-" + columnKey);
    }

    public Locator cells(String columnKey) {
        return page.getByTestId("result-" + columnKey);
    }

    public ClaimDetailPage openClaim(String claimNumber) {
        row(claimNumber).getByTestId("result-claim-link").click();
        return new ClaimDetailPage(page);
    }

    public ClaimDetailPage openFirstClaim() {
        claimNumbers().first().click();
        return new ClaimDetailPage(page);
    }

    /** open the claims by table row number, starting at 0. Fails if the row does not exist */
    public ClaimDetailPage openClaimRow(int claimNumber) {
        rows().all().get(claimNumber).click();
        return new ClaimDetailPage(page);
    }

    public SearchPage setPageSize(int size) {
        page.getByTestId("page-size").selectOption(String.valueOf(size));
        return this;
    }

    public SearchPage nextPage() {
        nextPageButton().click();
        return this;
    }

    public Locator queryInput() {
        return page.getByTestId("search-input");
    }

    public Locator scopeSelect() {
        return page.getByTestId("scope-select");
    }

    public Locator searchButton() {
        return page.getByTestId("search-button");
    }

    public Locator rows() {
        return page.getByTestId("result-row");
    }

    public Locator row(String claimNumber) {
        return rows().filter(new Locator.FilterOptions().setHasText(claimNumber));
    }

    public Locator claimNumbers() {
        return page.getByTestId("result-claim-link");
    }

    public Locator emptyState() {
        return page.getByTestId("empty-state");
    }

    public Locator errorMessage() {
        return page.getByTestId("error-message");
    }

    public Locator resultsSummary() {
        return page.getByTestId("results-summary");
    }

    public Locator pageIndicator() {
        return page.getByTestId("page-indicator");
    }

    public Locator nextPageButton() {
        return page.getByTestId("next-page");
    }

    public Locator previousPageButton() {
        return page.getByTestId("previous-page");
    }
}
