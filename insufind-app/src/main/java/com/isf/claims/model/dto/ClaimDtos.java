package com.isf.claims.model.dto;

import com.isf.claims.model.ClaimStatus;
import com.isf.claims.model.ClaimType;
import java.time.LocalDate;
import java.util.List;

/** API shapes. Entities never leave the service. */
public final class ClaimDtos {

    private ClaimDtos() {
    }

    public record DriverDto(Long id, String fullName, String licenseNumber) {
    }

    public record ClaimSummary(Long id, String claimNumber, LocalDate incidentDate, ClaimStatus status,
                               ClaimType claimType, String lossState, String policyNumber,
                               LocalDate reportedDate, String vehiclePlate, String vehicleVin,
                               String policyState,
                               DriverDto insuredDriver, List<DriverDto> otherDrivers) {
    }

    public record ClaimDetail(Long id, String claimNumber, String policyNumber, LocalDate incidentDate,
                              String location, ClaimStatus status, String description,
                              ClaimType claimType, LocalDate reportedDate, String vehiclePlate, String vehicleVin,
                              String policyState, String lossState,
                              DriverDto insuredDriver, List<DriverDto> otherDrivers) {
    }

    public record ClaimPage(List<ClaimSummary> items, int page, int size, long totalItems, int totalPages) {
    }
}
