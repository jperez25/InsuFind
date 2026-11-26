package com.isf.claims.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String claimNumber;

    private String policyNumber;

    private LocalDate incidentDate;

    private String location;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private String description;

    @Enumerated(EnumType.STRING)
    private ClaimType claimType;

    /** First notice of loss: when the claim was reported. */
    private LocalDate reportedDate;

    private String vehiclePlate;

    private String vehicleVin;

    /** Two-letter state that issued the policy. */
    private String policyState;

    /** Two-letter state where the loss happened. */
    private String lossState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insured_driver_id")
    private Driver insuredDriver;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "claim_other_driver",
            joinColumns = @JoinColumn(name = "claim_id"),
            inverseJoinColumns = @JoinColumn(name = "driver_id"))
    private List<Driver> otherDrivers = new ArrayList<>();
}
