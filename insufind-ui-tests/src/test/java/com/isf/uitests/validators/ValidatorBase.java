package com.isf.uitests.validators;

/**
 * Fluent base: every check returns the validator so calls chain, and {@link #and()} hands back the page so the
 * test can keep navigating. Checks use Playwright's retrying assertions.
 */
abstract class ValidatorBase<SELF extends ValidatorBase<SELF, PAGE>, PAGE> {

    protected final PAGE page;

    protected ValidatorBase(PAGE page) {
        this.page = page;
    }

    /** Back to the page object to continue the journey. */
    public PAGE and() {
        return page;
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }
}
