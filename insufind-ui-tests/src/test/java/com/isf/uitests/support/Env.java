package com.isf.uitests.support;

/** Test configuration. System property wins, then environment variable, then default. */
public final class Env {

    private Env() {
    }

    public static String baseUrl() {
        return read("baseUrl", "BASE_URL", "http://localhost:5173");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(read("headless", "HEADLESS", "true"));
    }

    public static String browser() {
        return read("browser", "BROWSER", "chromium");
    }

    private static String read(String property, String variable, String fallback) {
        String fromProperty = System.getProperty(property);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty;
        }
        String fromEnvironment = System.getenv(variable);
        return fromEnvironment != null && !fromEnvironment.isBlank() ? fromEnvironment : fallback;
    }
}
