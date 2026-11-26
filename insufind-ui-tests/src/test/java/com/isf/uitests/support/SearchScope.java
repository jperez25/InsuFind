package com.isf.uitests.support;

/** Values of the scope selector in the search page. */
public enum SearchScope {
    ANY("ANY"),
    INSURED("INSURED"),
    OTHER("OTHER");

    private final String optionValue;

    SearchScope(String optionValue) {
        this.optionValue = optionValue;
    }

    public String optionValue() {
        return optionValue;
    }
}
