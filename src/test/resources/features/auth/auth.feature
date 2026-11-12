@run @auth
Feature: Authentication
  The admin needs a token to update or delete bookings

  @smoke @regression @TC-2001
  Scenario: Admin gets a token with valid credentials
    When the user requests a token with valid admin credentials
    Then the response status code should be 200
    And a token should be returned

  @negative @regression @TC-2002
  Scenario Outline: No token is returned for invalid credentials
    When the user requests a token with username "<username>" and password "<password>"
    Then the response status code should be 200
    And no token should be returned and the reason should be "Bad credentials"

    Examples:
      | username | password      |
      | admin    | wrongpassword |
      | unknown  | password123   |
      |          |               |
