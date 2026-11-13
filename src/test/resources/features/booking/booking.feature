@run @booking
Feature: Booking API
  As a hotel system user
  I want to create, read, update and delete bookings
  So that reservations are managed correctly

  @smoke @regression @TC-1001
  Scenario: Create a booking with valid data
    Given a valid booking request is prepared
    When the user creates the booking
    Then the response status code should be 200
    And the response should contain the sent booking data
    And the response should match the "create-booking" schema
    And the response time should be less than 5000 ms

  @regression @TC-1002
  Scenario: Get an existing booking by id
    Given an existing booking
    When the user gets the booking
    Then the response status code should be 200
    And the response should match the "booking" schema

  @regression @TC-1003
  Scenario: Admin updates the first name of a booking
    Given an existing booking
    When the admin updates the booking's first name to "Recep"
    Then the response status code should be 200
    And the booking first name should be "Recep"

  @regression @TC-1004
  Scenario: Admin deletes a booking
    Given an existing booking
    When the admin deletes the booking
    Then the response status code should be 201
    And the booking should no longer be found

  @security @regression @TC-1005
  Scenario: A booking cannot be deleted without a token
    Given an existing booking
    When the user deletes the booking without a token
    Then the response status code should be 403

  @regression @TC-1006
  Scenario Outline: Create bookings with different total prices
    Given a booking request with total price <price> is prepared
    When the user creates the booking
    Then the response status code should be 200
    When the user gets the booking
    Then the booking total price should be <price>

    Examples:
      | price |
      | 1     |
      | 550   |
      | 9999  |

  @regression @TC-1007
  Scenario: Admin replaces all data of a booking
    Given an existing booking
    When the admin replaces the booking with new data
    Then the response status code should be 200
    And the booking should match the sent data
    When the user gets the booking
    Then the booking should match the sent data
