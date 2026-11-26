package com.isf.claims.repository;

import com.isf.claims.model.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClaimRepository extends JpaRepository<Claim, Long>, JpaSpecificationExecutor<Claim> {

    /**
     * Matches claims whose insured driver and/or other drivers match the pattern by name or license number.
     * With {@code includeClaimNumber} the claim number matches too. Newest incident first, id as tie-breaker.
     */
    @Query("""
            select distinct c from Claim c
              join c.insuredDriver i
              left join c.otherDrivers o
            where (:includeInsured = true
                     and (lower(i.fullName) like :pattern escape '\\' or lower(i.licenseNumber) like :pattern escape '\\'))
               or (:includeOther = true
                     and (lower(o.fullName) like :pattern escape '\\' or lower(o.licenseNumber) like :pattern escape '\\'))
               or (:includeClaimNumber = true and lower(c.claimNumber) like :pattern escape '\\')
            order by c.incidentDate desc, c.id desc
            """)
    Page<Claim> search(@Param("pattern") String pattern,
                       @Param("includeInsured") boolean includeInsured,
                       @Param("includeOther") boolean includeOther,
                       @Param("includeClaimNumber") boolean includeClaimNumber,
                       Pageable pageable);
}
