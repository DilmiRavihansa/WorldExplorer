package com.worldexplorer.net;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Single place for all outgoing HTTP traffic (interoperability).
 * Every external service (REST Countries, flag CDN, Fish API) goes through here, so
 * timeouts, the User-Agent and the cookie jar are configured once.
 * The shared CookieManager keeps any cookies a service sets and sends them back (HTTP cookies).
 */
public final class HttpSupport {
    private static final CookieManager COOKIES = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .cookieHandler(COOKIES)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    private static final String USER_AGENT = "WorldExplorer/2.0 (Java Swing coursework)";

    private HttpSupport() { }

    private static HttpRequest.Builder request(String url, Map<String, String> headers) {
        HttpRequest.Builder b = HttpRequest.newBuilder().uri(URI.create(url))
                .timeout(Duration.ofSeconds(15)).header("User-Agent", USER_AGENT).GET();
        if (headers != null) headers.forEach(b::header);
        return b;
    }

    /** GET a text resource (JSON / CSV). The status code is left to the caller. */
    public static HttpResponse<String> getString(String url, Map<String, String> headers)
            throws IOException, InterruptedException {
        return CLIENT.send(request(url, headers).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    /** GET a binary resource (images). Returns null unless the status is 200. */
    public static byte[] getBytes(String url) throws IOException, InterruptedException {
        HttpResponse<byte[]> r = CLIENT.send(request(url, null).build(), HttpResponse.BodyHandlers.ofByteArray());
        return r.statusCode() == 200 ? r.body() : null;
    }

    /** Cookies currently stored for this session (useful for diagnostics). */
    public static List<HttpCookie> cookies() { return COOKIES.getCookieStore().getCookies(); }
}
