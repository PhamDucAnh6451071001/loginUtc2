package com.example.loginutc2.e2e.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(Long.getLong("utc.timeoutSeconds", 15L)));
    }

    protected final WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected final String text(By locator) {
        return visible(locator).getText();
    }

    protected final String attribute(By locator, String name) {
        return visible(locator).getDomAttribute(name);
    }

    protected final void type(By locator, String value) {
        WebElement input = wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.elementToBeClickable(locator)));
        input.clear();
        input.sendKeys(value);
    }


    public final String title() {
        return driver.getTitle();
    }
}
