package com.isf.uitests.tests;

import static com.isf.uitests.support.TestGroups.REGRESSION;

import com.isf.uitests.base.BaseUiTest;
import com.isf.uitests.support.SeedData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** The claim detail page: drivers and load error. */
class ClaimDetailTest extends BaseUiTest {

    @Test
    @Tag(REGRESSION)
    void detailListsTheOtherDrivers() {
        searchPage()
                .searchByVin(SeedData.VIN_OF_CLAIM_1)
                .openClaim(SeedData.CLAIM_1)
                .validate()
                .hasOtherDriverContaining(SeedData.OTHER_DRIVER_OF_CLAIM_1);
    }

    @Test
    @Tag(REGRESSION)
    void detailShowsNoneWhenThereAreNoOtherDrivers() {
        searchPage()
                .searchFor(SeedData.CLAIM_WITHOUT_OTHER_DRIVERS)
                .openClaim(SeedData.CLAIM_WITHOUT_OTHER_DRIVERS)
                .validate()
                .hasClaimNumber(SeedData.CLAIM_WITHOUT_OTHER_DRIVERS)
                .hasNoOtherDrivers();
    }

    @Test
    @Tag(REGRESSION)
    void backendFailureShowsALoadError() {
        claimDetailPage()
                .failClaimApi()
                .open(SeedData.CLAIM_1_ID)
                .validate()
                .showsLoadError();
    }
}
