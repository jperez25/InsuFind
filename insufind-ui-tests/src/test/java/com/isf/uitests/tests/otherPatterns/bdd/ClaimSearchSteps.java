package com.isf.uitests.tests.otherPatterns.bdd;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.isf.uitests.pages.SearchPage;
import com.microsoft.playwright.Page;

/**
 * Step definitions in business language. With Cucumber these methods would carry
 * {@code @Given("...")}, {@code @When("...")}, {@code @Then("...")} annotations and be bound to a .feature file.
 * Here they are plain methods so the example needs no extra dependency.
 */
class ClaimSearchSteps {

    private final Page page;
    private SearchPage searchPage;

    ClaimSearchSteps(Page page) {
        this.page = page;
    }

    // Given an adjuster is on the search page
    void givenAnAdjusterIsOnTheSearchPage() {
        searchPage = new SearchPage(page).open();
    }

    // When they search for "<text>"
    void whenTheySearchFor(String text) {
        searchPage.searchFor(text);
    }

    // Then every result mentions "<text>"
    void thenEveryResultMentions(String text) {
        assertThat(searchPage.rows().first()).isVisible();
        for (var row : searchPage.rows().all()) {
            assertThat(row).containsText(text);
        }
    }

    // Then they are told that nothing matched
    void thenTheyAreToldNothingMatched() {
        assertThat(searchPage.emptyState()).isVisible();
    }
}
