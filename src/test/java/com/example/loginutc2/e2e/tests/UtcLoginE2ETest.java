package com.example.loginutc2.e2e.tests;

import java.net.URI;

import com.example.loginutc2.e2e.base.BaseTest;
import com.example.loginutc2.e2e.pages.LoginPage;
import com.example.loginutc2.e2e.pages.PasswordRecoveryPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

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

    @Test
    @DisplayName("TC02 - Nhập lại dữ liệu phải xóa giá trị cũ")
    void tc02_typingReplacesPreviousInput() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.enterUsername("old-user").enterPassword("old-test-value");
        loginPage.enterUsername("selenium_e2e_nonexistent").enterPassword("new-test-value");

        assertThat(loginPage.usernameValue()).isEqualTo("selenium_e2e_nonexistent");
        assertThat(loginPage.passwordValue()).isEqualTo("new-test-value");
    }

    @Test
    @DisplayName("TC03 - Form trống báo thiếu tên đăng nhập")
    void tc03_emptyFormShowsUsernameError() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.submitExpectingError();

        assertThat(loginPage.errorMessage()).isEqualTo("Bạn chưa nhập tên đăng nhập");
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC04 - Thiếu mật khẩu hiển thị thông báo phù hợp")
    void tc04_missingPasswordShowsPasswordError() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.enterUsername("selenium_e2e_nonexistent").submitExpectingError();

        assertThat(loginPage.errorMessage()).isEqualTo("Bạn chưa nhập mật khẩu");
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC05 - Thông tin không hợp lệ bị từ chối và ở lại Login")
    void tc05_invalidCredentialsStayOnLoginPage() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError("selenium_e2e_nonexistent", "invalid-test-value");

        assertThat(loginPage.errorMessage()).isEqualTo("Tài khoản hoặc mật khẩu không đúng.");
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC06 - Checkbox ẩn được điều khiển bằng label hiển thị")
    void tc06_rememberMeUsesVisibleLabel() {
        LoginPage loginPage = new LoginPage(driver).open();

        assertThat(loginPage.isNativeRememberMeVisible()).isFalse();
        assertThat(loginPage.isRememberMeSelected()).isFalse();

        loginPage.setRememberMe(true);
        assertThat(loginPage.isRememberMeSelected()).isTrue();
        loginPage.setRememberMe(true);
        assertThat(loginPage.isRememberMeSelected()).isTrue();

        loginPage.setRememberMe(false);
        assertThat(loginPage.isRememberMeSelected()).isFalse();
        loginPage.setRememberMe(false);
        assertThat(loginPage.isRememberMeSelected()).isFalse();
    }

    @Test
    @DisplayName("TC07 - Mở trang quên mật khẩu và trở lại đăng nhập")
    void tc07_forgotPasswordOpensRecoveryAndReturns() {
        LoginPage loginPage = new LoginPage(driver).open();

        PasswordRecoveryPage recoveryPage = loginPage.openPasswordRecovery();

        assertThat(recoveryPage.isOnRecoveryPage()).isTrue();
        assertThat(recoveryPage.title()).isEqualTo("Lấy lại mật khẩu");
        assertThat(recoveryPage.backToLoginText()).isEqualTo("Trở lại đăng nhập?");

        LoginPage returnedPage = recoveryPage.backToLogin();
        assertThat(returnedPage.isOnLoginPage()).isTrue();
        assertThat(returnedPage.isSubmitEnabled()).isTrue();
    }

    @Test
    @Tag("external-tab")
    @DisplayName("TC08 - Chuyển sang tab trợ giúp rồi đóng và quay về tab chính")
    void tc08_helpOpensNewTabAndReturns() {
        LoginPage loginPage = new LoginPage(driver).open();
        String mainTab = driver.getWindowHandle();
        assertThat(loginPage.helpLinkTarget()).isEqualTo("_blank");

        try {
            loginPage.openHelpInNewTab();

            assertThat(driver.getWindowHandle()).isNotEqualTo(mainTab);
            assertThat(driver.getWindowHandles()).hasSize(2);
            assertThat(URI.create(driver.getCurrentUrl()).getHost()).isEqualTo("hotrokythuat.utc.edu.vn");
        } finally {
            loginPage.closeExtraTabsAndReturnTo(mainTab);
        }

        assertThat(driver.getWindowHandles()).containsExactly(mainTab);
        assertThat(driver.getWindowHandle()).isEqualTo(mainTab);
        assertThat(loginPage.isOnLoginPage()).isTrue();
        assertThat(loginPage.isSubmitEnabled()).isTrue();
    }

}
