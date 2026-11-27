package com.isf.uitests.tests;

import static com.isf.uitests.support.TestGroups.REGRESSION;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Page size and pager navigation. */
class PaginationTest extends BaseUiTest {

    @ParameterizedTest(name = "page size {0}")
    @ValueSource(ints = {10, 20, 25, 50})
    @Tag(REGRESSION)
    void pageSizeControlsRowsAndPageCount(int size) {
        searchPage()
                .setPageSize(size)
                .searchFor("")
                .validate()
                .hasAtMostRows(size)
                .pagerMatchesSummaryFor(size)
                .isOnPage(1);
    }

    @Test
    @Tag(REGRESSION)
    void previousGoesBackToThePreviousPage() {
        searchPage()
                .setPageSize(SeedData.SMALL_PAGE_SIZE)
                .searchFor("")
                .nextPage()
                .validate()
                .isOnPage(2)
                .and()
                .previousPage()
                .validate()
                .isOnPage(1)
                .hasPreviousPageEnabled(false)
                .hasNextPageEnabled(true);
    }
}
