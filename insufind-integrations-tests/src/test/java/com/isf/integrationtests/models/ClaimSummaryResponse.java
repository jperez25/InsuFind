package com.isf.integrationtests.models;

import com.isf.integrationtests.constants.ClaimStatus;
import java.time.LocalDate;
import java.util.List;

/** One item of the claim list/search response. */
public record ClaimSummaryResponse(Long id, String claimNumber, LocalDate incidentDate, ClaimStatus status,
                                   DriverResponse insuredDriver, List<DriverResponse> otherDrivers) {
}
