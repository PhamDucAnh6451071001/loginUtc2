package com.example.loginutc2.e2e.pages;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
    private final By helpLink = By.cssSelector("a[href='http://hotrokythuat.utc.edu.vn']");

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
        return awaitErrorResponse(originalForm);
    }

    public LoginPage submitWithEnterExpectingError() {
        WebElement originalForm = visible(loginForm);
        visible(passwordField).sendKeys(Keys.ENTER);
        return awaitErrorResponse(originalForm);
    }

    private LoginPage awaitErrorResponse(WebElement originalForm) {
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

    public String helpLinkTarget() {
        return attribute(helpLink, "target");
    }

    public void openHelpInNewTab() {
        Set<String> originalTabs = new HashSet<>(driver.getWindowHandles());
        click(helpLink);
        wait.until(ExpectedConditions.numberOfWindowsToBe(originalTabs.size() + 1));
        String helpTab = driver.getWindowHandles().stream()
                .filter(handle -> !originalTabs.contains(handle))
                .findFirst().orElseThrow();
        driver.switchTo().window(helpTab);
        wait.until(browser -> "hotrokythuat.utc.edu.vn".equals(
                URI.create(browser.getCurrentUrl()).getHost()));
    }

    public LoginPage closeExtraTabsAndReturnTo(String mainTab) {
        for (String handle : new HashSet<>(driver.getWindowHandles())) {
            if (!handle.equals(mainTab)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        driver.switchTo().window(mainTab);
        return awaitReady();
    }
}
