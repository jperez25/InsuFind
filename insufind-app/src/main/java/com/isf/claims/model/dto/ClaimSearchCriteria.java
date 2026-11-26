package com.isf.claims.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isf.claims.model.ClaimStatus;
import com.isf.claims.model.ClaimType;
import com.isf.claims.model.SearchScope;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * Request body of {@code POST /api/claims/search}. Every field is optional; a null or empty filter is ignored and
 * all filters present are combined with AND. {@code q} with {@code scope} is the free-text driver/claim-number match.
 * Add new filters here only as optional fields so existing callers keep working.
 */
public record ClaimSearchCriteria(
        @Size(max = 100) String q,
        SearchScope scope,
        @Size(max = 10) List<ClaimStatus> statuses,
        @Size(max = 10) List<ClaimType> claimTypes,
        LocalDate lossDateFrom,
        LocalDate lossDateTo,
        LocalDate reportedFrom,
        LocalDate reportedTo,
        @Pattern(regexp = "[A-Za-z]{2}", message = "must be a two-letter state code") String policyState,
        @Pattern(regexp = "[A-Za-z]{2}", message = "must be a two-letter state code") String lossState,
        @Size(max = 15) String vehiclePlate,
        @Size(max = 17) String vehicleVin,
        @Size(max = 30) String policyNumber) {

    @JsonIgnore
    @AssertTrue(message = "lossDateFrom must not be after lossDateTo")
    public boolean isLossDateRangeValid() {
        return lossDateFrom == null || lossDateTo == null || !lossDateFrom.isAfter(lossDateTo);
    }

    @JsonIgnore
    @AssertTrue(message = "reportedFrom must not be after reportedTo")
    public boolean isReportedRangeValid() {
        return reportedFrom == null || reportedTo == null || !reportedFrom.isAfter(reportedTo);
    }
}
