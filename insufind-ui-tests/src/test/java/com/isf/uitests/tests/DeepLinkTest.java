package com.isf.uitests.tests;

import static com.isf.uitests.support.TestGroups.REGRESSION;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SearchScope;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** The whole search lives in the URL, so a link or a reload restores it. */
class DeepLinkTest extends BaseUiTest {

    @Test
    @Tag(REGRESSION)
    void driverSearchLinkRestoresQueryScopeAndResults() {
        searchPage()
                .openWith("tab=driver&q=" + SeedData.MARIA.replace(" ", "%20") + "&scope=INSURED")
                .validate()
                .hasQueryValue(SeedData.MARIA)
                .hasScope(SearchScope.INSURED)
                .allCellsContain("insured", SeedData.MARIA);
    }

    @Test
    @Tag(REGRESSION)
    void periodInTheLinkOpensTheOptionalSectionAndFiltersResults() {
        searchPage()
                .openWith("tab=vehicle&vin=" + SeedData.VIN_OF_CLAIM_1 + "&lossFrom=" + SeedData.LOSS_DATE_CLAIM_1
                        + "&lossTo=" + SeedData.LOSS_DATE_CLAIM_1)
                .validate()
                .optionalFieldsAreOpen()
                .lossPeriodIs(SeedData.LOSS_DATE_CLAIM_1, SeedData.LOSS_DATE_CLAIM_1)
                .listsExactly(SeedData.CLAIM_1);
    }

    @Test
    @Tag(REGRESSION)
    void pageAndSizeInTheLinkRestoreThatPage() {
        searchPage()
                .openWith("tab=driver&size=" + SeedData.SMALL_PAGE_SIZE + "&page=1")
                .validate()
                .isOnPage(2)
                .hasPreviousPageEnabled(true);
    }

    @Test
    @Tag(REGRESSION)
    void reloadingKeepsTheSearch() {
        searchPage()
                .searchFor(SeedData.MARIA)
                .reload()
                .validate()
                .hasQueryValue(SeedData.MARIA)
                .allRowsContain(SeedData.MARIA);
    }
}
