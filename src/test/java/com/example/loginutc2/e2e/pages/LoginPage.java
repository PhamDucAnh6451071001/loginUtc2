package com.example.loginutc2.e2e.pages;

import java.net.URI;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public final class LoginPage extends BasePage {

    public static final String URL = "https://vanphongdientu.utc.edu.vn/Login";
    private final By usernameField = By.name("username");
    private final By passwordField = By.name("userpwd");
    private final By loginButton = By.cssSelector("input.submit_login");
    private final By forgotPasswordLink = By.cssSelector("a[href='/Login/GetPass']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(URL);
        return awaitReady();
    }

    public LoginPage awaitReady() {
        visible(usernameField);
        visible(passwordField);
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        return this;
    }

    public boolean isOnLoginPage() {
        URI current = URI.create(driver.getCurrentUrl());
        return "vanphongdientu.utc.edu.vn".equals(current.getHost())
                && "/Login".equals(current.getPath());
    }

    public String usernamePlaceholder() {
        return attribute(usernameField, "placeholder");
    }

    public String passwordPlaceholder() {
        return attribute(passwordField, "placeholder");
    }

    public String passwordInputType() {
        return attribute(passwordField, "type");
    }

    public String submitLabel() {
        return attribute(loginButton, "value");
    }

    public boolean isSubmitVisible() {
        return visible(loginButton).isDisplayed();
    }

    public boolean isSubmitEnabled() {
        return visible(loginButton).isEnabled();
    }

    public String forgotPasswordText() {
        return text(forgotPasswordLink);
    }
}
