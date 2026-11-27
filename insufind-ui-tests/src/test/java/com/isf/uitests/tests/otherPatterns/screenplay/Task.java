package com.isf.uitests.tests.otherPatterns.screenplay;

/** A business-level action ("search for claims"). Built from low-level steps. */
@FunctionalInterface
public interface Task {
    void performAs(Actor actor);
}
