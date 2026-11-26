package com.isf.integrationtests.models;

import com.isf.integrationtests.constants.ClaimStatus;
import java.time.LocalDate;
import java.util.List;

/** Body of GET /api/claims/{id}. */
public record ClaimDetailResponse(Long id, String claimNumber, String policyNumber, LocalDate incidentDate,
                                  String location, ClaimStatus status, String description,
                                  DriverResponse insuredDriver, List<DriverResponse> otherDrivers) {
}
