package com.raykurt.api.auth;

import com.raykurt.api.config.ConfigReader;
import com.raykurt.api.models.AuthRequest;
import com.raykurt.api.models.AuthResponse;
import com.raykurt.api.specs.RequestSpecFactory;

import java.time.Duration;
import java.time.Instant;

import static io.restassured.RestAssured.given;

/**
 * Common authentication method used by all step definitions that need a token.
 * The admin token is cached and reused until it is older than TOKEN_TTL,
 * so parallel scenarios do not call /auth again and again.
 */
public final class AuthHelper {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(10);

    private static String cachedToken;
    private static Instant createdAt;

    private AuthHelper() {
    }

    public static synchronized String getAdminToken() {
        boolean expired = createdAt == null || Instant.now().isAfter(createdAt.plus(TOKEN_TTL));
        if (cachedToken == null || expired) {
            cachedToken = requestNewToken();
            createdAt = Instant.now();
        }
        return cachedToken;
    }

    /** Forces a new token on the next call, e.g. after a 403 caused by an expired token. */
    public static synchronized void clearCache() {
        cachedToken = null;
        createdAt = null;
    }

    private static String requestNewToken() {
        AuthRequest credentials = new AuthRequest(
                ConfigReader.get("admin.username"),
                ConfigReader.get("admin.password"));

        AuthResponse response = given()
                .spec(RequestSpecFactory.defaultSpec())
                .body(credentials)
            .when()
                .post(ConfigReader.get("endpoint.auth"))
            .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);

        if (response.getToken() == null) {
            throw new IllegalStateException("Admin token could not be created: " + response.getReason());
        }
        return response.getToken();
    }
}
