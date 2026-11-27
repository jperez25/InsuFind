package com.isf.uitests.tests.otherPatterns.screenplay;

/** Reads something from the system. Never changes it. */
@FunctionalInterface
public interface Question<T> {
    T answeredBy(Actor actor);
}
