package com.raykurt.api.bodies;

import com.raykurt.api.models.Booking;
import com.raykurt.api.models.BookingDates;
import net.datafaker.Faker;

import java.time.LocalDate;

/**
 * Factory methods that create request bodies with dynamic test data.
 * Step definitions never build bodies by hand; they call these methods.
 */
public final class BookingBody {

    // Faker is not guaranteed to be thread-safe, and scenarios run in parallel.
    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(Faker::new);

    private BookingBody() {
    }

    public static Booking validBooking() {
        Faker faker = FAKER.get();
        LocalDate checkin = LocalDate.now().plusDays(faker.number().numberBetween(1, 30));
        LocalDate checkout = checkin.plusDays(faker.number().numberBetween(1, 7));

        Booking booking = new Booking();
        booking.setFirstname(faker.name().firstName());
        booking.setLastname(faker.name().lastName());
        booking.setTotalprice(faker.number().numberBetween(100, 1000));
        booking.setDepositpaid(true);
        booking.setBookingdates(new BookingDates(checkin.toString(), checkout.toString()));
        booking.setAdditionalneeds("Breakfast");
        return booking;
    }

    public static Booking bookingWithPrice(int totalPrice) {
        Booking booking = validBooking();
        booking.setTotalprice(totalPrice);
        return booking;
    }

    /** Body for a partial update (PATCH): only the first name is sent. */
    public static Booking firstNameUpdate(String firstName) {
        Booking booking = new Booking();
        booking.setFirstname(firstName);
        return booking;
    }
}
