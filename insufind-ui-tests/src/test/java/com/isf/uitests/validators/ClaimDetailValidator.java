package com.isf.uitests.validators;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.isf.uitests.pages.ClaimDetailPage;
import com.isf.uitests.support.ClaimStatus;

/** Fluent checks for the claim detail page. */
public class ClaimDetailValidator extends ValidatorBase<ClaimDetailValidator, ClaimDetailPage> {

    private ClaimDetailValidator(ClaimDetailPage page) {
        super(page);
    }

    public static ClaimDetailValidator validate(ClaimDetailPage page) {
        return new ClaimDetailValidator(page);
    }

    public ClaimDetailValidator hasClaimNumber(String expected) {
        assertThat(page.claimNumber()).hasText(expected);
        return this;
    }

    public ClaimDetailValidator hasStatus(ClaimStatus expected) {
        assertThat(page.status()).hasText(expected.label());
        return this;
    }

    public ClaimDetailValidator hasPolicyNumber(String expected) {
        assertThat(page.policyNumber()).hasText(expected);
        return this;
    }

    public ClaimDetailValidator hasInsuredDriverContaining(String text) {
        assertThat(page.insuredDriver()).containsText(text);
        return this;
    }

    /** The license number appears among the insured or other drivers. */
    public ClaimDetailValidator involvesLicense(String licenseNumber) {
        assertThat(page.root()).containsText(licenseNumber);
        return this;
    }

    public ClaimDetailValidator hasVin(String expected) {
        assertThat(page.vin()).hasText(expected);
        return this;
    }

    public ClaimDetailValidator hasPlate(String expected) {
        assertThat(page.plate()).hasText(expected);
        return this;
    }

    /** The extra detail fields are all rendered with a value (a dash when the claim has none). */
    public ClaimDetailValidator showsAllDetailFields() {
        assertThat(page.type()).not().isEmpty();
        assertThat(page.reportedDate()).not().isEmpty();
        assertThat(page.lossState()).not().isEmpty();
        assertThat(page.policyState()).not().isEmpty();
        assertThat(page.plate()).not().isEmpty();
        assertThat(page.vin()).not().isEmpty();
        return this;
    }

    public ClaimDetailValidator hasOtherDriverContaining(String text) {
        assertThat(page.otherDrivers().first()).containsText(text);
        return this;
    }

    public ClaimDetailValidator hasNoOtherDrivers() {
        assertThat(page.noOtherDrivers()).isVisible();
        assertThat(page.otherDrivers()).hasCount(0);
        return this;
    }

    public ClaimDetailValidator showsLoadError() {
        assertThat(page.errorMessage()).isVisible();
        assertThat(page.root()).hasCount(0);
        return this;
    }

    public ClaimDetailValidator isNotFound() {
        assertThat(page.notFound()).isVisible();
        assertThat(page.notFound()).containsText("Oops! Claim does not exist");
        return this;
    }
}
