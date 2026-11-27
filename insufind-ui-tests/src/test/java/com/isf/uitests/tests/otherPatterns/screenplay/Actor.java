package com.isf.uitests.tests.otherPatterns.screenplay;

import java.util.HashMap;
import java.util.Map;

/** Someone using the system. Has abilities, performs tasks, answers questions. */
public class Actor {

    private final String name;
    private final Map<Class<? extends Ability>, Ability> abilities = new HashMap<>();

    private Actor(String name) {
        this.name = name;
    }

    public static Actor named(String name) {
        return new Actor(name);
    }

    public Actor whoCan(Ability ability) {
        abilities.put(ability.getClass(), ability);
        return this;
    }

    public <A extends Ability> A abilityTo(Class<A> type) {
        Ability ability = abilities.get(type);
        if (ability == null) {
            throw new IllegalStateException(name + " cannot " + type.getSimpleName());
        }
        return type.cast(ability);
    }

    public Actor attemptsTo(Task... tasks) {
        for (Task task : tasks) {
            task.performAs(this);
        }
        return this;
    }

    public <T> T asksFor(Question<T> question) {
        return question.answeredBy(this);
    }
}
