package com.example.loginutc2.e2e.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

public final class ScreenshotWatcher implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Throwable failure = context.getExecutionException().orElse(null);
        if (failure == null || failure instanceof TestAbortedException) {
            return;
        }
        WebDriver driver = ((BaseTest) context.getRequiredTestInstance()).currentDriver();
        if (!(driver instanceof TakesScreenshot screenshot)) {
            return;
        }
        try {
            Path directory = Path.of("build", "screenshots");
            Files.createDirectories(directory);
            Path destination = directory.resolve(context.getRequiredTestMethod().getName()
                    + "-" + System.nanoTime() + ".png");
            Files.write(destination, screenshot.getScreenshotAs(OutputType.BYTES));
        } catch (IOException | WebDriverException screenshotFailure) {
            failure.addSuppressed(screenshotFailure);
        }
    }
}
