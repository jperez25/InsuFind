package com.isf.claims.service;

import com.isf.claims.model.Claim;
import com.isf.claims.model.Driver;
import com.isf.claims.model.SearchScope;
import com.isf.claims.model.dto.ClaimDtos.ClaimDetail;
import com.isf.claims.model.dto.ClaimDtos.ClaimPage;
import com.isf.claims.model.dto.ClaimDtos.ClaimSummary;
import com.isf.claims.model.dto.ClaimSearchCriteria;
import com.isf.claims.model.dto.ClaimDtos.DriverDto;
import com.isf.claims.repository.ClaimRepository;
import com.isf.claims.repository.ClaimSpecifications;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClaimService {

    private final ClaimRepository claims;

    public ClaimService(ClaimRepository claims) {
        this.claims = claims;
    }

    public ClaimPage search(String query, SearchScope scope, int page, int size) {
        String pattern = "%" + ClaimSpecifications.escapeLike(query == null ? "" : query.trim().toLowerCase(Locale.ROOT)) + "%";
        boolean includeInsured = scope != SearchScope.OTHER;
        boolean includeOther = scope != SearchScope.INSURED;
        boolean includeClaimNumber = scope == SearchScope.ANY;
        return toPage(claims.search(pattern, includeInsured, includeOther, includeClaimNumber,
                PageRequest.of(page, size)));
    }

    /** Multi-filter search. Newest incident first, id as tie-breaker. */
    public ClaimPage search(ClaimSearchCriteria criteria, int page, int size) {
        Sort order = Sort.by(Sort.Order.desc("incidentDate"), Sort.Order.desc("id"));
        return toPage(claims.findAll(ClaimSpecifications.matching(criteria), PageRequest.of(page, size, order)));
    }

    private static ClaimPage toPage(Page<Claim> result) {
        List<ClaimSummary> items = result.getContent().stream().map(ClaimService::toSummary).toList();
        return new ClaimPage(items, result.getNumber(), result.getSize(), result.getTotalElements(),
                result.getTotalPages());
    }

    public Optional<ClaimDetail> find(long id) {
        return claims.findById(id).map(ClaimService::toDetail);
    }

    private static DriverDto toDto(Driver driver) {
        return new DriverDto(driver.getId(), driver.getFullName(), driver.getLicenseNumber());
    }

    private static List<DriverDto> toDtos(List<Driver> drivers) {
        return drivers.stream().map(ClaimService::toDto).toList();
    }

    private static ClaimSummary toSummary(Claim claim) {
        return new ClaimSummary(claim.getId(), claim.getClaimNumber(), claim.getIncidentDate(), claim.getStatus(),
                claim.getClaimType(), claim.getLossState(), claim.getPolicyNumber(),
                claim.getReportedDate(), claim.getVehiclePlate(), claim.getVehicleVin(), claim.getPolicyState(),
                toDto(claim.getInsuredDriver()), toDtos(claim.getOtherDrivers()));
    }

    private static ClaimDetail toDetail(Claim claim) {
        return new ClaimDetail(claim.getId(), claim.getClaimNumber(), claim.getPolicyNumber(),
                claim.getIncidentDate(), claim.getLocation(), claim.getStatus(), claim.getDescription(),
                claim.getClaimType(), claim.getReportedDate(), claim.getVehiclePlate(), claim.getVehicleVin(),
                claim.getPolicyState(), claim.getLossState(),
                toDto(claim.getInsuredDriver()), toDtos(claim.getOtherDrivers()));
    }
}
