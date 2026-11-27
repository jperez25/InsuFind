package com.isf.uitests.pages;

import com.isf.uitests.validators.ClaimDetailValidator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.regex.Pattern;

/** The claim detail page. Acts and reads, never asserts. */
public class ClaimDetailPage {

    private final Page page;

    public ClaimDetailPage(Page page) {
        this.page = page;
    }

    /** Opens the detail route directly (e.g. for a claim that does not exist). */
    public ClaimDetailPage open(long claimId) {
        page.navigate("/claims/" + claimId);
        return this;
    }

    /** Makes loading a claim fail with a 500. Call before opening. */
    public ClaimDetailPage failClaimApi() {
        page.route(Pattern.compile(".*/api/claims/\\d+$"), route -> route.fulfill(
                new com.microsoft.playwright.Route.FulfillOptions().setStatus(500).setBody("{}")));
        return this;
    }

    public Locator noOtherDrivers() {
        return page.getByTestId("detail-no-other-drivers");
    }

    public Locator errorMessage() {
        return page.getByTestId("error-message");
    }

    public ClaimDetailValidator validate() {
        return ClaimDetailValidator.validate(this);
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

    public Locator policyNumber() {return page.getByTestId("detail-policy");}

    public Locator insuredDriver() {
        return page.getByTestId("detail-insured-driver");
    }

    public Locator otherDrivers() {
        return page.getByTestId("detail-other-driver");
    }

    public Locator type() {
        return page.getByTestId("detail-type");
    }

    public Locator reportedDate() {
        return page.getByTestId("detail-reported-date");
    }

    public Locator lossState() {
        return page.getByTestId("detail-loss-state");
    }

    public Locator policyState() {
        return page.getByTestId("detail-policy-state");
    }

    public Locator plate() {
        return page.getByTestId("detail-plate");
    }

    public Locator vin() {
        return page.getByTestId("detail-vin");
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
