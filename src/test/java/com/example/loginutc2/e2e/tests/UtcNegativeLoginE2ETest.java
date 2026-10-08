package com.example.loginutc2.e2e.tests;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import com.example.loginutc2.e2e.base.BaseTest;
import com.example.loginutc2.e2e.base.BrowserNetwork;
import com.example.loginutc2.e2e.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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

    @Test
    @DisplayName("TC16 - Password chỉ có dấu cách bị từ chối")
    void tc16_spacesOnlyPasswordIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError(UNKNOWN_USER, "   ");

        // No trimming policy is specified; either controlled rejection is valid.
        assertThat(loginPage.errorMessage()).isIn(
                "Bạn chưa nhập mật khẩu", INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC17 - Username Unicode không tồn tại bị từ chối")
    void tc17_unicodeUsernameIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError("selenium_qa_không_tồn_tại_98765", DUMMY_PASSWORD);

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC18 - Password Unicode sai bị từ chối")
    void tc18_unicodePasswordIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError(UNKNOWN_USER, "sai_mật_khẩu_để_kiểm_thử_98765");

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC19 - Username dạng email giả bị từ chối")
    void tc19_emailShapedUsernameIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError("selenium_e2e_nonexistent@example.invalid", DUMMY_PASSWORD);

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC20 - Username chỉ gồm số giả bị từ chối")
    void tc20_numericDummyUsernameIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError("0".repeat(30), DUMMY_PASSWORD);

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC21 - Password chỉ gồm ký tự đặc biệt bị từ chối")
    void tc21_punctuationOnlyPasswordIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.loginExpectingError(UNKNOWN_USER, "!@#$%^&*()_+-=[]{};:,.?");

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC22 - Password sai có dấu cách hai đầu bị từ chối")
    void tc22_paddedInvalidPasswordIsRejected() {
        String password = " " + DUMMY_PASSWORD + " ";
        LoginPage loginPage = new LoginPage(driver).open()
                .enterUsername(UNKNOWN_USER).enterPassword(password);

        assertThat(loginPage.passwordValue()).isEqualTo(password);
        loginPage.submitExpectingError();

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC23 - Username dài 1.024 ký tự bị từ chối có kiểm soát")
    void tc23_usernameWith1024CharactersIsRejected() {
        String value = "u".repeat(1024);
        LoginPage loginPage = new LoginPage(driver).open()
                .enterUsername(value).enterPassword(DUMMY_PASSWORD);

        assertThat(loginPage.usernameValue()).isEqualTo(value);
        loginPage.submitExpectingError();

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC24 - Password dài 1.024 ký tự bị từ chối có kiểm soát")
    void tc24_passwordWith1024CharactersIsRejected() {
        String value = "p".repeat(1024);
        LoginPage loginPage = new LoginPage(driver).open()
                .enterUsername(UNKNOWN_USER).enterPassword(value);

        assertThat(loginPage.passwordValue()).isEqualTo(value);
        loginPage.submitExpectingError();

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC25 - Nhấn Enter với thông tin sai vẫn bị từ chối")
    void tc25_invalidLoginSubmittedWithEnterIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open()
                .enterUsername(UNKNOWN_USER).enterPassword(DUMMY_PASSWORD);

        loginPage.submitWithEnterExpectingError();

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @DisplayName("TC26 - Ghi nhớ đăng nhập không làm thông tin sai được chấp nhận")
    void tc26_invalidLoginWithRememberMeIsRejected() {
        LoginPage loginPage = new LoginPage(driver).open()
                .enterUsername(UNKNOWN_USER).enterPassword(DUMMY_PASSWORD)
                .setRememberMe(true);

        assertThat(loginPage.isRememberMeSelected()).isTrue();
        loginPage.submitExpectingError();

        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }

    @Test
    @Tag("network")
    @DisplayName("TC27 - HTTP chuyển HTTPS trước khi gửi POST chứa mật khẩu giả")
    void tc27_httpRedirectAndCredentialPostUseHttps() throws Exception {
        URI httpLogin = URI.create(LoginPage.URL.replace("https://", "http://"));
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(15)).build();
        HttpRequest request = HttpRequest.newBuilder(httpLogin)
                .timeout(Duration.ofSeconds(15)).GET().build();

        // A direct HTTP check distinguishes the server redirect from Chrome auto-upgrade.
        HttpResponse<Void> redirect = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertThat(redirect.statusCode()).isIn(301, 302, 307, 308);
        URI destination = httpLogin.resolve(redirect.headers().firstValue("location").orElseThrow());
        assertThat(destination.getScheme()).isEqualTo("https");
        assertThat(destination.getHost()).isEqualTo("vanphongdientu.utc.edu.vn");
        assertThat(destination.getPath()).isEqualTo("/Login");

        BrowserNetwork network = new BrowserNetwork(driver);
        LoginPage loginPage = new LoginPage(driver).openFromHttp();
        assertThat(URI.create(driver.getCurrentUrl()).getScheme()).isEqualTo("https");
        loginPage.loginExpectingError(UNKNOWN_USER, DUMMY_PASSWORD);

        List<BrowserNetwork.Request> posts = network.loginPosts();
        assertThat(posts).as("Captured actual login POSTs").hasSize(1);
        assertThat(posts.get(0).hasPasswordBody()).isTrue();
        assertThat(URI.create(posts.get(0).url()).getScheme()).isEqualTo("https");
        assertThat(loginPage.errorMessage()).isEqualTo(INVALID_CREDENTIALS);
        assertThat(loginPage.isOnLoginPage()).isTrue();
    }
}
