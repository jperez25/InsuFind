package com.isf.integrationtests.models;

import java.util.List;

/** Body of GET /api/claims. */
public record ClaimPageResponse(List<ClaimSummaryResponse> items, int page, int size, long totalItems, int totalPages) {
}
