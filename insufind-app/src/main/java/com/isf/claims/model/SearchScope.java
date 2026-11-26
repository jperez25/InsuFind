package com.isf.claims.model;

/** Which drivers a search looks at. */
public enum SearchScope {
    /** The insured driver, the other drivers and the claim number. */
    ANY,
    /** Only the insured driver (policy holder). */
    INSURED,
    /** Only the other drivers involved in the incident. */
    OTHER
}
