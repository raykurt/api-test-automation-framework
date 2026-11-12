package com.raykurt.api.steps;

import com.raykurt.api.context.ScenarioContext;
import io.cucumber.java.en.Then;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/** Shared response validations, reused by every feature. */
public class CommonSteps {

    private final ScenarioContext context;

    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertThat(context.getResponse().getStatusCode())
                .as("Response body: %s", context.getResponse().asString())
                .isEqualTo(expectedStatusCode);
    }

    @Then("the response should match the {string} schema")
    public void theResponseShouldMatchTheSchema(String schemaName) {
        context.getResponse()
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/" + schemaName + ".json"));
    }

    @Then("the response time should be less than {long} ms")
    public void theResponseTimeShouldBeLessThan(long maxMillis) {
        assertThat(context.getResponse().getTime()).isLessThan(maxMillis);
    }
}
