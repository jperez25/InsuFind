package com.isf.uitests.tests.otherPatterns.screenplay;

import static com.isf.uitests.tests.otherPatterns.screenplay.Targets.*;

import com.microsoft.playwright.Page;

/** The things an adjuster does, in business words. */
public final class Tasks {

    private Tasks() {
    }

    public static Task openTheSearchPage() {
        return actor -> BrowseTheWeb.as(actor).navigate("/");
    }

    public static Task searchForClaims(String text) {
        return actor -> {
            Page page = BrowseTheWeb.as(actor);
            page.getByTestId(SEARCH_INPUT).fill(text);
            page.getByTestId(SEARCH_BUTTON).click();
        };
    }

    public static Task openTheFirstClaim() {
        return actor -> BrowseTheWeb.as(actor).getByTestId(CLAIM_LINK).first().click();
    }
}
