package com.example.loginutc2.e2e.pages;

import java.net.URI;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public final class LoginPage extends BasePage {

    public static final String URL = "https://vanphongdientu.utc.edu.vn/Login";
    private final By usernameField = By.name("username");
    private final By passwordField = By.name("userpwd");
    private final By loginButton = By.cssSelector("input.submit_login");
    private final By forgotPasswordLink = By.cssSelector("a[href='/Login/GetPass']");
    private final By loginForm = By.cssSelector("form[action='/Login']");
    private final By errorMessage = By.cssSelector(".form .error");
    private final By rememberMeCheckbox = By.id("persistent");
    private final By rememberMeLabel = By.cssSelector("label.check[for='persistent']");

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

    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    public String usernameValue() {
        return visible(usernameField).getDomProperty("value");
    }

    public String passwordValue() {
        return visible(passwordField).getDomProperty("value");
    }

    public LoginPage submitExpectingError() {
        WebElement originalForm = visible(loginForm);
        click(loginButton);
        // The live UTC form performs a full POST, so wait for its response.
        wait.until(ExpectedConditions.stalenessOf(originalForm));
        visible(errorMessage);
        return awaitReady();
    }

    public String errorMessage() {
        return text(errorMessage);
    }

    public LoginPage loginExpectingError(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return submitExpectingError();
    }

    public boolean isNativeRememberMeVisible() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(rememberMeCheckbox)).isDisplayed();
    }

    public boolean isRememberMeSelected() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(rememberMeCheckbox)).isSelected();
    }

    public LoginPage setRememberMe(boolean selected) {
        if (isRememberMeSelected() != selected) {
            click(rememberMeLabel);
        }
        wait.until(ExpectedConditions.elementSelectionStateToBe(rememberMeCheckbox, selected));
        return this;
    }

    public PasswordRecoveryPage openPasswordRecovery() {
        click(forgotPasswordLink);
        return new PasswordRecoveryPage(driver).awaitReady();
    }
}
