package com.isf.uitests.support;

/** Claim type: the key used in test IDs and the label shown in the UI. */
public enum ClaimType {
    COLLISION("Collision"),
    THEFT("Theft"),
    GLASS("Glass"),
    WEATHER("Weather"),
    VANDALISM("Vandalism"),
    OTHER("Other");

    private final String label;

    ClaimType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
