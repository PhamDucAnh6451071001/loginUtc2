package com.example.loginutc2.e2e.tests;

import com.example.loginutc2.e2e.base.BaseTest;
import com.example.loginutc2.e2e.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtcLoginE2ETest extends BaseTest {

    @Test
    @DisplayName("TC01 - Trang đăng nhập hiển thị form và thuộc tính đúng")
    void tc01_loginPageShowsExpectedForm() {
        LoginPage loginPage = new LoginPage(driver).open();

        assertThat(loginPage.isOnLoginPage()).isTrue();
        assertThat(loginPage.title()).isEqualTo("Đăng nhập");
        assertThat(loginPage.usernamePlaceholder()).isEqualTo("Tên đăng nhập");
        assertThat(loginPage.passwordPlaceholder()).isEqualTo("Mật khẩu");
        assertThat(loginPage.passwordInputType()).isEqualTo("password");
        assertThat(loginPage.submitLabel()).isEqualTo("Đăng nhập");
        assertThat(loginPage.isSubmitVisible()).isTrue();
        assertThat(loginPage.isSubmitEnabled()).isTrue();
        assertThat(loginPage.forgotPasswordText()).isEqualTo("Bạn quên mật khẩu đăng nhập ?");
    }
}
