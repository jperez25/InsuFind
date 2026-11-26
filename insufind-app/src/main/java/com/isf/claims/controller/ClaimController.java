package com.isf.claims.controller;

import com.isf.claims.model.SearchScope;
import com.isf.claims.model.dto.ClaimDtos.ClaimDetail;
import com.isf.claims.model.dto.ClaimDtos.ClaimPage;
import com.isf.claims.model.dto.ClaimSearchCriteria;
import com.isf.claims.service.ClaimService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/claims")
@Validated
public class ClaimController {

    private final ClaimService service;

    public ClaimController(ClaimService service) {
        this.service = service;
    }

    @GetMapping
    public ClaimPage search(@RequestParam(defaultValue = "") String q,
                            @RequestParam(defaultValue = "ANY") SearchScope scope,
                            @RequestParam(defaultValue = "0") @Min(0) int page,
                            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int size) {
        return service.search(q, scope, page, size);
    }

    @PostMapping("/search")
    public ClaimPage search(@RequestBody @Valid ClaimSearchCriteria criteria,
                            @RequestParam(defaultValue = "0") @Min(0) int page,
                            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int size) {
        return service.search(criteria, page, size);
    }

    @GetMapping("/{id}")
    public ClaimDetail get(@PathVariable long id) {
        return service.find(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
    }
}
