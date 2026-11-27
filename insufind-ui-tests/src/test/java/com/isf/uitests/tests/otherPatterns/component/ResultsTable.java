package com.isf.uitests.tests.otherPatterns.component;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Component object: models one reusable piece of UI (the results table), not a whole page.
 * Any page that shows the table can compose it. Like page objects, it reads and acts but never asserts.
 */
public class ResultsTable {

    private final Page page;

    public ResultsTable(Page page) {
        this.page = page;
    }

    public Locator root() {
        return page.getByTestId("results-table");
    }

    public Locator rows() {
        return root().getByTestId("result-row");
    }

    public Locator column(String columnKey) {
        return root().getByTestId("result-" + columnKey);
    }

    public Locator header(String columnKey) {
        return root().getByTestId("header-" + columnKey);
    }

    public ResultsTable sortBy(String columnKey) {
        page.getByTestId("sort-" + columnKey).click();
        return this;
    }

    public Locator rowOf(String claimNumber) {
        return rows().filter(new Locator.FilterOptions().setHasText(claimNumber));
    }
}
