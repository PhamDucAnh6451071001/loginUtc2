package com.example.loginutc2.e2e.base;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@ExtendWith(ScreenshotWatcher.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void startBrowser(TestInfo testInfo) {
        ChromeOptions options = new ChromeOptions();
        if (testInfo.getTags().contains("external-tab")) {
            // TC08 verifies the new tab and its URL, without awaiting all help-site resources.
            options.setPageLoadStrategy(PageLoadStrategy.NONE);
        }
        if (Boolean.parseBoolean(System.getProperty("utc.headless", "true"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080", "--no-first-run");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        // Keep implicit wait at its default zero; Page Objects use explicit waits.
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }

    final WebDriver currentDriver() {
        return driver;
    }
}
