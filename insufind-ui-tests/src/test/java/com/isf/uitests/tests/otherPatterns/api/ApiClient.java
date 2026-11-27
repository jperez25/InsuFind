package com.isf.uitests.tests.otherPatterns.api;

import com.isf.uitests.support.Env;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

/**
 * Thin HTTP client on Playwright's request context (no browser). Goes through the same base URL as the UI,
 * so it uses the Vite proxy. Only for setup/checks; it never imports backend code.
 */
class ApiClient implements AutoCloseable {

    private final Playwright playwright = Playwright.create();
    private final APIRequestContext request = playwright.request()
            .newContext(new APIRequest.NewContextOptions().setBaseURL(Env.baseUrl()));

    APIResponse search(String json, int page, int size) {
        return request.post("/api/claims/search?page=" + page + "&size=" + size,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(json));
    }

    @Override
    public void close() {
        request.dispose();
        playwright.close();
    }
}
