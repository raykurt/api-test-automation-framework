package com.raykurt.api.context;

import com.raykurt.api.models.Booking;
import io.restassured.response.Response;

/**
 * Holds the state of one scenario (request body, last response, booking id).
 * PicoContainer creates a new instance for every scenario and injects it into
 * every step class, so scenarios can run in parallel without sharing state.
 */
public class ScenarioContext {

    private Booking requestBody;
    private Response response;
    private Integer bookingId;

    public Booking getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Booking requestBody) {
        this.requestBody = requestBody;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Integer getBookingId() {
        if (bookingId == null) {
            throw new IllegalStateException("No booking id in this scenario. Create a booking first.");
        }
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
}
