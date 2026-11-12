@manual @booking
Feature: Booking API - manual checks
  Checks run by hand. They are tagged @manual, so the default tag filter
  (not @manual) keeps them out of every automated run, while they stay
  next to the automated scenarios and keep their test case IDs.

  @TC-1901
  Scenario: Booking dates are shown correctly in the hotel admin screen
    Given a booking is created through the API
    When the tester opens the booking in the admin screen
    Then the check-in and check-out dates should match the API data

  @TC-1902
  Scenario: Response headers do not expose server details
    Given any booking endpoint is called
    Then the response headers should be reviewed for version or server information
