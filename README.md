# API Test Automation Framework

![API Tests](https://github.com/raykurt/api-test-automation-framework/actions/workflows/api-tests.yml/badge.svg)

A BDD-style REST API test automation framework built with **Java 17, Cucumber 7, REST Assured and JUnit 5**.
It tests the public [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) API and shows how I structure
API test automation: readable scenarios, reusable steps, POJO models, dynamic test data, tag-based execution,
parallel runs and CI with GitHub Actions.

## Tech stack

| Area | Tools |
|---|---|
| Language | Java 17 |
| BDD | Cucumber 7 (Gherkin), PicoContainer for dependency injection |
| Runner | JUnit 5 Platform Suite (`@Suite`, `@IncludeEngines("cucumber")`) |
| API client | REST Assured, JSON Schema Validator |
| Models | Jackson (POJO serialization) |
| Assertions | AssertJ |
| Test data | Datafaker |
| Async waits | Awaitility |
| Build | Maven |
| CI | GitHub Actions (push, pull request, nightly smoke run, manual run with tags) |

## Project structure

```
src/test/java/com/raykurt/api
├── auth/        AuthHelper              → one common method to get the admin token (cached, thread-safe)
├── bodies/      BookingBody             → factory methods that build request bodies with test data
├── config/      ConfigReader            → config.properties + config-<env>.properties + -D overrides
├── context/     ScenarioContext         → per-scenario state, injected into all step classes
├── hooks/       Hooks                   → logging on failure, attaches the last response to the report
├── models/      Booking, AuthRequest... → POJO request/response models
├── runners/     BookingRunner, AuthRunner → service-specific JUnit 5 suites
├── specs/       RequestSpecFactory      → shared request specification
├── steps/       BookingSteps, AuthSteps, CommonSteps
└── utils/       WaitUtils               → Awaitility polling instead of Thread.sleep

src/test/resources
├── features/    booking/, auth/         → Gherkin scenarios, tagged with TC IDs (@run automated, @manual manual)
├── schemas/                             → JSON schemas for contract-style response checks
├── config.properties                    → shared values
├── config-ci.properties                 → CI environment values (-Denv=ci)
└── junit-platform.properties            → parallel execution, default tag filter

ci-examples/                             → the same run as an Azure DevOps pipeline and a Jenkinsfile
```

## Design decisions

- **REST Assured calls live in the step definitions**, starting from a shared `RequestSpecFactory`, so each step shows exactly which request it sends.
- **Step classes are split per service**, and common validations (status code, schema, response time) are in `CommonSteps` and reused by every feature.
- **Request bodies are created by factory methods** (`BookingBody`) instead of inline JSON, with random data from Datafaker.
- **State is kept in `ScenarioContext`**, injected by PicoContainer. Each scenario gets its own instance, so scenarios can run in parallel safely.
- **Manual and automated scenarios live side by side.** Automated features are tagged `@run`; manual checks are tagged `@manual` and excluded by the default filter `not @manual and not @wip`, so they keep their test case IDs without ever running in CI.
- **Configuration in layers.** `config.properties` holds shared values, `config-<env>.properties` the values of one environment (`-Denv=ci`), and any single value can be overridden with `-D`.
- **No fixed sleeps.** Asynchronous results are polled with Awaitility (`WaitUtils.waitUntil`), with timeout and interval taken from the configuration.
- **The admin token is cached** for 10 minutes and shared by all scenarios, so parallel runs do not hammer the auth endpoint.
- **Every scenario is tagged** with a test case ID (`@TC-1001`) and a suite tag (`@smoke`, `@regression`, `@security`, `@negative`), so any subset can be run from the command line or CI.

## Running the tests

Requirements: Java 17+ and Maven.

```bash
# All automated tests (manual ones are excluded by default)
mvn test

# With the CI environment configuration
mvn test -Denv=ci

# Only smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Regression without security tests
mvn test -Dcucumber.filter.tags="@regression and not @security"

# A single test case
mvn test -Dcucumber.filter.tags="@TC-1004"

# Only one service
mvn test -Dtest=BookingRunner

# Against another environment
mvn test -Dbase.url=http://localhost:3001
```

Reports are written to `target/cucumber-reports/` (HTML and JSON). In GitHub Actions they are uploaded as the `cucumber-reports` artifact.

## Test coverage

| ID | Scenario | Tags |
|---|---|---|
| TC-1001 | Create a booking with valid data (body, schema, response time) | smoke, regression |
| TC-1002 | Get an existing booking by id | regression |
| TC-1003 | Admin updates the first name of a booking (PATCH with token) | regression |
| TC-1004 | Admin deletes a booking, then it returns 404 (polled with Awaitility) | regression |
| TC-1005 | A booking cannot be deleted without a token (403) | security, regression |
| TC-1006 | Create bookings with different total prices (Scenario Outline) | regression |
| TC-1007 | Admin replaces all data of a booking, then GET returns the same data | regression |
| TC-2001 | Admin gets a token with valid credentials | smoke, regression |
| TC-2002 | No token for invalid credentials (Scenario Outline) | negative, regression |
| TC-1901–1902 | Manual checks (admin screen dates, response headers) | manual |

> Restful Booker is a public practice API and is reset regularly, so an occasional failure caused by the service itself is possible.
