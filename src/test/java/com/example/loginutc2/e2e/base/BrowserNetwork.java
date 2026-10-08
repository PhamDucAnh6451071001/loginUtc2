package com.example.loginutc2.e2e.base;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.example.loginutc2.e2e.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

/** Observes tagged Chrome tests in memory; never writes raw network logs or cookies. */
public final class BrowserNetwork {

    private final ChromeDriver driver;
    private final Json json = new Json();
    private final List<Request> requests = new ArrayList<>();
    private final List<String> redirectUrls = new ArrayList<>();

    public BrowserNetwork(WebDriver driver) {
        if (!(driver instanceof ChromeDriver chromeDriver)) {
            throw new IllegalArgumentException("Chrome Network capture requires ChromeDriver");
        }
        this.driver = chromeDriver;
        this.driver.executeCdpCommand("Network.enable", Map.of());
    }

    public List<Request> loginPosts() {
        collect();
        URI login = URI.create(LoginPage.URL);
        return requests.stream()
                .filter(request -> "POST".equals(request.method()))
                .filter(request -> {
                    URI url = URI.create(request.url());
                    // Include HTTP and query variants so transport/URL leaks cannot be filtered out.
                    return login.getHost().equals(url.getHost()) && login.getPath().equals(url.getPath());
                })
                .toList();
    }

    public String responseBody(Request request) {
        Map<String, Object> response = driver.executeCdpCommand(
                "Network.getResponseBody", Map.of("requestId", request.requestId()));
        if (!(response.get("body") instanceof String body)) {
            throw new IllegalStateException("Captured login response has no readable body");
        }
        return Boolean.TRUE.equals(response.get("base64Encoded"))
                ? new String(Base64.getDecoder().decode(body), StandardCharsets.UTF_8)
                : body;
    }

    public List<String> observedUrls() {
        collect();
        List<String> urls = new ArrayList<>(redirectUrls);
        requests.forEach(request -> urls.add(request.url()));
        return List.copyOf(urls);
    }

    private void collect() {
        for (LogEntry entry : driver.manage().logs().get(LogType.PERFORMANCE)) {
            Map<?, ?> root = json.toType(entry.getMessage(), Map.class);
            if (!(root.get("message") instanceof Map<?, ?> message)
                    || !"Network.requestWillBeSent".equals(message.get("method"))
                    || !(message.get("params") instanceof Map<?, ?> params)
                    || !(params.get("request") instanceof Map<?, ?> request)
                    || !(params.get("requestId") instanceof String requestId)
                    || !(request.get("method") instanceof String method)
                    || !(request.get("url") instanceof String url)) {
                continue;
            }
            // Retain only the presence of the password field, never its body value.
            boolean hasPasswordBody = request.get("postData") instanceof String body
                    && body.contains("userpwd=");
            requests.add(new Request(requestId, method, url, hasPasswordBody));
            if (params.get("redirectResponse") instanceof Map<?, ?> redirect
                    && redirect.get("url") instanceof String redirectUrl) {
                redirectUrls.add(redirectUrl);
            }
        }
    }

    public record Request(String requestId, String method, String url, boolean hasPasswordBody) {
    }
}
