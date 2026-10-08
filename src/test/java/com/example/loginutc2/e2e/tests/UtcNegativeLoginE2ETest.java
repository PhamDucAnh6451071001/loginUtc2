package com.example.loginutc2.e2e.tests;

import com.example.loginutc2.e2e.base.BaseTest;
import com.example.loginutc2.e2e.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtcNegativeLoginE2ETest extends BaseTest {

    private static final String UNKNOWN_USER = "selenium_e2e_nonexistent";
    private static final String DUMMY_PASSWORD = "invalid-test-value";
    private static final String INVALID_CREDENTIALS = "Tài khoản hoặc mật khẩu không đúng.";

    @Test
    @DisplayName("TC10 - Username có khoảng trắng giữa bị từ chối")
    void tc10_usernameWithInternalSpacesIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError("selenium e2e nonexistent", DUMMY_PASSWORD);

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC11 - Username có khoảng trắng hai đầu vẫn bị từ chối")
    void tc11_paddedUnknownUsernameIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError(" " + UNKNOWN_USER + " ", DUMMY_PASSWORD);

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC12 - Username chỉ có ký tự đặc biệt bị từ chối")
    void tc12_punctuationOnlyUsernameIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError("@#$%^", DUMMY_PASSWORD);

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC13 - Username dài 256 ký tự bị từ chối có kiểm soát")
    void tc13_usernameWith256CharactersIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError("u".repeat(256), DUMMY_PASSWORD);

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC14 - Password dài 256 ký tự bị từ chối có kiểm soát")
    void tc14_passwordWith256CharactersIsRejected() {
        String password = "p".repeat(256);
        LoginPage loginPage = new LoginPage(driver).open()
                .enterUsername(UNKNOWN_USER).enterPassword(password);

        assertThat(loginPage.passwordValue()).isEqualTo(password);
        loginPage.submitExpectingError();

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC15 - Username chỉ có dấu cách bị từ chối")
    void tc15_spacesOnlyUsernameIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError("   ", DUMMY_PASSWORD);

        // No trimming policy is specified; either controlled rejection is valid.
        assertThat(loginPage.errorMessage()).isIn(
                "Bạn chưa nhập tên đăng nhập", INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }
}
