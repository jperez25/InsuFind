package com.isf.uitests.tests.otherPatterns.inline;

import static com.isf.uitests.support.TestGroups.REGRESSION;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PATTERN: plain test, locators inline, no page objects, no validators.
 * Everything the test does is visible in the method. Fine for a handful of tests; the cost shows up when the same
 * locator is repeated in many tests (a renamed data-testid then breaks all of them).
 */
class InlineLocatorsTest extends BaseUiTest {

    @Test
    @Tag(REGRESSION)
    void searchByDriverNameShowsTheirClaims() {
        page.navigate("/");

        page.getByTestId("search-input").fill(SeedData.MARIA);
        page.getByTestId("search-button").click();

        assertThat(page.getByTestId("result-row").first()).isVisible();
        for (var row : page.getByTestId("result-row").all()) {
            assertThat(row).containsText(SeedData.MARIA);
        }
    }

    @Test
    @Tag(REGRESSION)
    void clickingAClaimNavigatesToItsDetail() {
        page.navigate("/");
        page.getByTestId("search-input").fill(SeedData.CLAIM_1);
        page.getByTestId("search-button").click();

        page.getByTestId("result-claim-link").first().click();

        // navigation check: URL + a page-specific element
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/claims/\\d+$"));
        assertThat(page.getByTestId("detail-claim-number")).hasText(SeedData.CLAIM_1);
    }
}
