package com.example.loginutc2.e2e.pages;

import java.net.URI;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public final class PasswordRecoveryPage extends BasePage {

    private final By backToLoginLink = By.cssSelector("a[href='/Login']");

    public PasswordRecoveryPage(WebDriver driver) {
        super(driver);
    }

    public PasswordRecoveryPage awaitReady() {
        wait.until(ExpectedConditions.urlToBe(LoginPage.URL + "/GetPass"));
        visible(backToLoginLink);
        return this;
    }

    public boolean isOnRecoveryPage() {
        URI current = URI.create(driver.getCurrentUrl());
        return "vanphongdientu.utc.edu.vn".equals(current.getHost())
                && "/Login/GetPass".equals(current.getPath());
    }

    public String backToLoginText() {
        return text(backToLoginLink);
    }

    public LoginPage backToLogin() {
        click(backToLoginLink);
        return new LoginPage(driver).awaitReady();
    }
}
