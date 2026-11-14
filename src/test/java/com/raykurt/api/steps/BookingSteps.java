package com.raykurt.api.steps;

import com.raykurt.api.auth.AuthHelper;
import com.raykurt.api.bodies.BookingBody;
import com.raykurt.api.config.ConfigReader;
import com.raykurt.api.context.ScenarioContext;
import com.raykurt.api.models.Booking;
import com.raykurt.api.models.CreateBookingResponse;
import com.raykurt.api.specs.RequestSpecFactory;
import com.raykurt.api.utils.WaitUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for the booking service.
 * REST Assured calls are made directly in the step methods; bodies come from
 * BookingBody factory methods and state is kept in the injected ScenarioContext.
 */
public class BookingSteps {

    private static final String BOOKING = ConfigReader.get("endpoint.booking");

    private final ScenarioContext context;

    public BookingSteps(ScenarioContext context) {
        this.context = context;
    }

    // ---------- Given ----------

    @Given("a valid booking request is prepared")
    public void aValidBookingRequestIsPrepared() {
        context.setRequestBody(BookingBody.validBooking());
    }

    @Given("a booking request with total price {int} is prepared")
    public void aBookingRequestWithTotalPriceIsPrepared(int totalPrice) {
        context.setRequestBody(BookingBody.bookingWithPrice(totalPrice));
    }

    @Given("an existing booking")
    public void anExistingBooking() {
        aValidBookingRequestIsPrepared();
        theUserCreatesTheBooking();
        assertThat(context.getResponse().getStatusCode())
                .as("Precondition failed: booking could not be created")
                .isEqualTo(200);
    }

    // ---------- When ----------

    @When("the user creates the booking")
    public void theUserCreatesTheBooking() {
        Response response = given()
                .spec(RequestSpecFactory.defaultSpec())
                .body(context.getRequestBody())
            .when()
                .post(BOOKING);

        context.setResponse(response);
        if (response.getStatusCode() == 200) {
            context.setBookingId(response.as(CreateBookingResponse.class).getBookingid());
        }
    }

    @When("the user gets the booking")
    public void theUserGetsTheBooking() {
        context.setResponse(
                given()
                    .spec(RequestSpecFactory.defaultSpec())
                    .pathParam("id", context.getBookingId())
                .when()
                    .get(BOOKING + "/{id}"));
    }

    @When("the admin updates the booking's first name to {string}")
    public void theAdminUpdatesTheFirstName(String firstName) {
        context.setResponse(
                given()
                    .spec(RequestSpecFactory.authorizedSpec(AuthHelper.getAdminToken()))
                    .pathParam("id", context.getBookingId())
                    .body(BookingBody.firstNameUpdate(firstName))
                .when()
                    .patch(BOOKING + "/{id}"));
    }

    @When("the admin replaces the booking with new data")
    public void theAdminReplacesTheBookingWithNewData() {
        context.setRequestBody(BookingBody.validBooking());
        context.setResponse(
                given()
                    .spec(RequestSpecFactory.authorizedSpec(AuthHelper.getAdminToken()))
                    .pathParam("id", context.getBookingId())
                    .body(context.getRequestBody())
                .when()
                    .put(BOOKING + "/{id}"));
    }

    @When("the user searches bookings by the guest's first and last name")
    public void theUserSearchesBookingsByTheGuestsName() {
        Booking sent = context.getRequestBody();
        context.setResponse(
                given()
                    .spec(RequestSpecFactory.defaultSpec())
                    .queryParam("firstname", sent.getFirstname())
                    .queryParam("lastname", sent.getLastname())
                .when()
                    .get(BOOKING));
    }

    @When("the admin deletes the booking")
    public void theAdminDeletesTheBooking() {
        context.setResponse(
                given()
                    .spec(RequestSpecFactory.authorizedSpec(AuthHelper.getAdminToken()))
                    .pathParam("id", context.getBookingId())
                .when()
                    .delete(BOOKING + "/{id}"));
    }

    @When("the user deletes the booking without a token")
    public void theUserDeletesTheBookingWithoutAToken() {
        context.setResponse(
                given()
                    .spec(RequestSpecFactory.defaultSpec())
                    .pathParam("id", context.getBookingId())
                .when()
                    .delete(BOOKING + "/{id}"));
    }

    // ---------- Then ----------

    @Then("the response should contain the sent booking data")
    public void theResponseShouldContainTheSentBookingData() {
        Booking actual = context.getResponse().as(CreateBookingResponse.class).getBooking();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(context.getRequestBody());
    }

    @Then("the search result should contain the created booking")
    public void theSearchResultShouldContainTheCreatedBooking() {
        List<Integer> ids = context.getResponse().jsonPath().getList("bookingid", Integer.class);
        assertThat(ids).contains(context.getBookingId());
    }

    @Then("the booking should match the sent data")
    public void theBookingShouldMatchTheSentData() {
        // PUT and GET return the booking object directly (not wrapped like POST)
        Booking actual = context.getResponse().as(Booking.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(context.getRequestBody());
    }

    @Then("the booking should no longer be found")
    public void theBookingShouldNoLongerBeFound() {
        Integer id = context.getBookingId();
        WaitUtils.waitUntil("booking " + id + " returns 404", () ->
                given()
                    .spec(RequestSpecFactory.defaultSpec())
                    .pathParam("id", id)
                .when()
                    .get(BOOKING + "/{id}")
                    .getStatusCode() == 404);
    }

    @Then("the booking first name should be {string}")
    public void theBookingFirstNameShouldBe(String expectedFirstName) {
        // GET and PATCH return the booking object directly (not wrapped like POST)
        Booking actual = context.getResponse().as(Booking.class);
        assertThat(actual.getFirstname()).isEqualTo(expectedFirstName);
    }

    @Then("the booking total price should be {int}")
    public void theBookingTotalPriceShouldBe(int expectedTotalPrice) {
        Booking actual = context.getResponse().as(Booking.class);
        assertThat(actual.getTotalprice()).isEqualTo(expectedTotalPrice);
    }
}
