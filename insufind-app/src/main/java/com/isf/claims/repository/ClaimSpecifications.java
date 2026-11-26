package com.isf.claims.repository;

import com.isf.claims.model.Claim;
import com.isf.claims.model.Driver;
import com.isf.claims.model.SearchScope;
import com.isf.claims.model.dto.ClaimSearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/** Builds the dynamic claim query: one predicate per filter present, all combined with AND. */
public final class ClaimSpecifications {

    private static final char ESCAPE = '\\';

    private ClaimSpecifications() {
    }

    public static Specification<Claim> matching(ClaimSearchCriteria c) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> all = new ArrayList<>();

            String text = c.q() == null ? "" : c.q().trim();
            if (!text.isEmpty()) {
                SearchScope scope = c.scope() == null ? SearchScope.ANY : c.scope();
                String pattern = "%" + escapeLike(text.toLowerCase(Locale.ROOT)) + "%";
                List<Predicate> any = new ArrayList<>();
                if (scope != SearchScope.OTHER) {
                    any.add(driverMatches(cb, root.join("insuredDriver"), pattern));
                }
                if (scope != SearchScope.INSURED) {
                    any.add(driverMatches(cb, root.join("otherDrivers", JoinType.LEFT), pattern));
                }
                if (scope == SearchScope.ANY) {
                    any.add(cb.like(cb.lower(root.get("claimNumber")), pattern, ESCAPE));
                }
                all.add(cb.or(any.toArray(Predicate[]::new)));
            }
            if (c.statuses() != null && !c.statuses().isEmpty()) {
                all.add(root.get("status").in(c.statuses()));
            }
            if (c.claimTypes() != null && !c.claimTypes().isEmpty()) {
                all.add(root.get("claimType").in(c.claimTypes()));
            }
            if (c.lossDateFrom() != null) {
                all.add(cb.greaterThanOrEqualTo(root.get("incidentDate"), c.lossDateFrom()));
            }
            if (c.lossDateTo() != null) {
                all.add(cb.lessThanOrEqualTo(root.get("incidentDate"), c.lossDateTo()));
            }
            if (c.reportedFrom() != null) {
                all.add(cb.greaterThanOrEqualTo(root.get("reportedDate"), c.reportedFrom()));
            }
            if (c.reportedTo() != null) {
                all.add(cb.lessThanOrEqualTo(root.get("reportedDate"), c.reportedTo()));
            }
            addEqualsIgnoreCase(all, cb, root.get("policyState"), c.policyState());
            addEqualsIgnoreCase(all, cb, root.get("lossState"), c.lossState());
            addEqualsIgnoreCase(all, cb, root.get("vehicleVin"), c.vehicleVin());
            addEqualsIgnoreCase(all, cb, root.get("policyNumber"), c.policyNumber());
            if (c.vehiclePlate() != null && !c.vehiclePlate().isBlank()) {
                String pattern = "%" + escapeLike(c.vehiclePlate().trim().toLowerCase(Locale.ROOT)) + "%";
                all.add(cb.like(cb.lower(root.get("vehiclePlate")), pattern, ESCAPE));
            }
            return cb.and(all.toArray(Predicate[]::new));
        };
    }

    public static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static Predicate driverMatches(CriteriaBuilder cb, Join<Claim, Driver> driver, String pattern) {
        return cb.or(cb.like(cb.lower(driver.get("fullName")), pattern, ESCAPE),
                cb.like(cb.lower(driver.get("licenseNumber")), pattern, ESCAPE));
    }

    private static void addEqualsIgnoreCase(List<Predicate> all, CriteriaBuilder cb, Expression<String> column,
                                            String value) {
        if (value != null && !value.isBlank()) {
            all.add(cb.equal(cb.lower(column), value.trim().toLowerCase(Locale.ROOT)));
        }
    }
}
