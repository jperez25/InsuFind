package com.isf.uitests.support;

/** Status text shown in the UI. */
public enum ClaimStatus {
    OPEN("Open"),
    IN_REVIEW("In review"),
    APPROVED("Approved"),
    DENIED("Denied"),
    CLOSED("Closed");

    private final String label;

    ClaimStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
