package com.isf.uitests.pages;

import com.isf.uitests.support.SearchScope;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The claim search page. Acts and reads; assertions belong to the tests. */
public class SearchPage {

    private final Page page;

    public SearchPage(Page page) {
        this.page = page;
    }

    public SearchPage open() {
        page.navigate("/");
        return this;
    }

    public SearchPage searchFor(String query) {
        queryInput().fill(query);
        searchButton().click();
        return this;
    }

    public SearchPage searchFor(String query, SearchScope scope) {
        scopeSelect().selectOption(scope.optionValue());
        return searchFor(query);
    }

    public SearchPage searchByVin(String vin) {
        page.getByTestId("tab-vehicle").click();
        page.getByTestId("vin-input").fill(vin);
        searchButton().click();
        return this;
    }

    public SearchPage showColumn(String columnKey) {
        page.getByTestId("columns-button").click();
        page.getByTestId("column-toggle-" + columnKey).check();
        return this;
    }

    public SearchPage sortBy(String columnKey) {
        page.getByTestId("sort-" + columnKey).click();
        return this;
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
