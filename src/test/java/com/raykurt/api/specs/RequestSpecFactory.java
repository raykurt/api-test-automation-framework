package com.raykurt.api.specs;

import com.raykurt.api.config.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Builds the shared request specification (base URI, content type, headers).
 * Every request in the step definitions starts from this spec.
 */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    public static RequestSpecification defaultSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.url"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /** Restful Booker expects the admin token as a cookie for update and delete calls. */
    public static RequestSpecification authorizedSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(defaultSpec())
                .addCookie("token", token)
                .build();
    }
}
