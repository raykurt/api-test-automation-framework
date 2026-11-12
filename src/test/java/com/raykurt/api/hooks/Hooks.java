package com.raykurt.api.hooks;

import com.raykurt.api.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;

import java.nio.charset.StandardCharsets;

public class Hooks {

    private final ScenarioContext context;

    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    @BeforeAll
    public static void beforeAll() {
        // Prints the full request and response to the console only when a check fails
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @After
    public void attachLastResponseOnFailure(Scenario scenario) {
        if (scenario.isFailed() && context.getResponse() != null) {
            scenario.attach(
                    context.getResponse().asString().getBytes(StandardCharsets.UTF_8),
                    "application/json",
                    "Last response");
        }
    }
}
