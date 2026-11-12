package com.raykurt.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** POST /booking wraps the created booking together with its new id. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBookingResponse {

    private Integer bookingid;
    private Booking booking;

    public Integer getBookingid() {
        return bookingid;
    }

    public void setBookingid(Integer bookingid) {
        this.bookingid = bookingid;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
