package com.raykurt.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Dates are kept as ISO strings (yyyy-MM-dd), exactly as the API returns them. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDates {

    private String checkin;
    private String checkout;

    public BookingDates() {
    }

    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }
}
