package com.isf.uitests.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The claim detail page. */
public class ClaimDetailPage {

    private final Page page;

    public ClaimDetailPage(Page page) {
        this.page = page;
    }

    public Locator root() {
        return page.getByTestId("claim-detail");
    }

    public Locator claimNumber() {
        return page.getByTestId("detail-claim-number");
    }

    public Locator status() {
        return page.getByTestId("detail-status");
    }

    public Locator insuredDriver() {
        return page.getByTestId("detail-insured-driver");
    }

    public Locator otherDrivers() {
        return page.getByTestId("detail-other-driver");
    }

    public Locator location() {
        return page.getByTestId("detail-location");
    }

    public Locator notFound() {
        return page.getByTestId("not-found");
    }

    public SearchPage backToSearch() {
        page.getByTestId("back-to-search").click();
        return new SearchPage(page);
    }
}
