package com.isf.uitests.tests.otherPatterns.screenplay;

import com.microsoft.playwright.Page;

/** Ability: the actor can drive a browser page. */
public class BrowseTheWeb implements Ability {

    private final Page page;

    private BrowseTheWeb(Page page) {
        this.page = page;
    }

    public static BrowseTheWeb with(Page page) {
        return new BrowseTheWeb(page);
    }

    public static Page as(Actor actor) {
        return actor.abilityTo(BrowseTheWeb.class).page;
    }
}
