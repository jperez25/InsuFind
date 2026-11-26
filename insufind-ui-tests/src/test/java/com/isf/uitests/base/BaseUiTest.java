package com.isf.uitests.base;

import com.isf.uitests.support.Env;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Browser lifecycle. One Playwright + Browser per test class, one fresh BrowserContext (and Page) per test,
 * so tests are isolated and classes can run in parallel. On failure the trace and a screenshot are saved.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseUiTest {

    private static final Path ARTIFACTS = Path.of("playwright-artifacts");

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private String testName;
    private boolean tracingStopped;

    protected Page page;

    @RegisterExtension
    final AfterTestExecutionCallback saveEvidenceOnFailure = extensionContext -> {
        if (extensionContext.getExecutionException().isPresent()) {
            page.screenshot(new Page.ScreenshotOptions().setPath(ARTIFACTS.resolve(testName + ".png")).setFullPage(true));
            context.tracing().stop(new Tracing.StopOptions().setPath(ARTIFACTS.resolve(testName + "-trace.zip")));
            tracingStopped = true;
        }
    };

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        BrowserType type = switch (Env.browser().toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();
        };
        browser = type.launch(new BrowserType.LaunchOptions().setHeadless(Env.headless()));
    }

    @AfterAll
    void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void openPage(TestInfo info) {
        testName = getClass().getSimpleName() + "." + info.getTestMethod().map(m -> m.getName()).orElse("test");
        context = browser.newContext(new Browser.NewContextOptions().setBaseURL(Env.baseUrl()));
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
        tracingStopped = false;
        page = context.newPage();
    }

    @AfterEach
    void closePage() {
        if (!tracingStopped) {
            context.tracing().stop();
        }
        context.close();
    }
}
