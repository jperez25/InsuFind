package com.isf.uitests.tests.otherPatterns.screenplay;

import static com.isf.uitests.tests.otherPatterns.screenplay.Targets.*;

import java.util.List;

/** What the adjuster can see. Each question waits for the screen to settle before it answers. */
public final class Questions {

    private Questions() {
    }

    /** Text of every result row (waits for at least one). */
    public static Question<List<String>> theResultRows() {
        return actor -> {
            var rows = BrowseTheWeb.as(actor).getByTestId(RESULT_ROW);
            rows.first().waitFor();
            return rows.allInnerTexts();
        };
    }

    public static Question<Boolean> theEmptyStateIsShown() {
        return actor -> {
            var empty = BrowseTheWeb.as(actor).getByTestId(EMPTY_STATE);
            empty.waitFor();
            return empty.isVisible();
        };
    }

    public static Question<String> theDisplayedClaimNumber() {
        return actor -> {
            var number = BrowseTheWeb.as(actor).getByTestId(DETAIL_CLAIM_NUMBER);
            number.waitFor();
            return number.innerText();
        };
    }
}
