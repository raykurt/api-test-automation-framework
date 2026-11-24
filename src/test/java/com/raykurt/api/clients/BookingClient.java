
package com.raykurt.api.clients;

import com.raykurt.api.config.ConfigReader;
import com.raykurt.api.models.Booking;
import com.raykurt.api.specs.RequestSpecFactory;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * API client for booking service operations.
 */
public class BookingClient {

    private static final String BOOKING =
            ConfigReader.get("endpoint.booking");

    public Response createBooking(Booking booking) {
        return given()
                .spec(RequestSpecFactory.defaultSpec())
                .body(booking)
            .when()
                .post(BOOKING);
    }
}
