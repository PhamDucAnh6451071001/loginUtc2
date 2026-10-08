package com.example.loginutc2.e2e.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(ScreenshotWatcher.class)
public abstract class BaseTest {

    private static final Map<String, String> LOCAL_ENV = readLocalEnvironment();
    protected WebDriver driver;

    @BeforeEach
    void startBrowser(TestInfo testInfo) {
        if (testInfo.getTags().contains("credentials")) {
            assumeTrue(!utcUser().isBlank() && !utcPassword().isBlank(),
                    "Configure UTC_USER and UTC_PASS in the environment or local .env to run TC09.");
        }

        ChromeOptions options = new ChromeOptions();
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

    protected final String utcUser() {
        return setting("UTC_USER");
    }

    protected final String utcPassword() {
        return setting("UTC_PASS");
    }

    private static String setting(String name) {
        String environmentValue = System.getenv(name);
        return environmentValue != null ? environmentValue : LOCAL_ENV.getOrDefault(name, "");
    }

    private static Map<String, String> readLocalEnvironment() {
        Path envFile = Path.of(".env");
        Map<String, String> values = new HashMap<>();
        if (!Files.exists(envFile)) {
            return values;
        }
        try {
            for (String rawLine : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
                String line = rawLine.strip();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int separator = line.indexOf('=');
                if (separator <= 0) {
                    throw new IllegalArgumentException("Each .env entry must use NAME=value.");
                }
                String name = line.substring(0, separator).strip();
                String value = line.substring(separator + 1).strip();
                if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                values.put(name, value);
            }
            return values;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read the local .env file.", exception);
        }
    }
}
