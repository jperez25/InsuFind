package com.isf.uitests.tests.otherPatterns.api;

import static com.isf.uitests.support.TestGroups.REGRESSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SeedData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PATTERN: push logic to the API layer. Validation rules and data correctness are checked over HTTP (fast, stable);
 * the browser is only used for what a browser is needed for. The second test is a hybrid: the API is the oracle
 * and the UI must agree with it. (Full black-box API tests live in insufind-integrations-tests.)
 */
class ApiVersusUiTest extends BaseUiTest {

    private static final Pattern TOTAL = Pattern.compile("\"totalItems\":(\\d+)");

    @Test
    @Tag(REGRESSION)
    void apiReturnsOnlyClaimsOfTheDriver() {
        try (ApiClient api = new ApiClient()) {
            var response = api.search("{\"q\":\"" + SeedData.MARIA + "\"}", 0, 5);

            assertTrue(response.ok());
            assertTrue(response.text().contains(SeedData.MARIA));
        }
    }

    @Test
    @Tag(REGRESSION)
    void uiTotalMatchesTheApiTotal() {
        int apiTotal;
        try (ApiClient api = new ApiClient()) {
            Matcher m = TOTAL.matcher(api.search("{\"q\":\"" + SeedData.MARIA + "\"}", 0, 1).text());
            assertTrue(m.find());
            apiTotal = Integer.parseInt(m.group(1));
        }

        int uiTotal = searchPage().searchFor(SeedData.MARIA).totalClaims();

        assertEquals(apiTotal, uiTotal);
    }
}
