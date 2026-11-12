package com.raykurt.api.steps;

import com.raykurt.api.config.ConfigReader;
import com.raykurt.api.context.ScenarioContext;
import com.raykurt.api.models.AuthRequest;
import com.raykurt.api.models.AuthResponse;
import com.raykurt.api.specs.RequestSpecFactory;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthSteps {

    private final ScenarioContext context;

    public AuthSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the user requests a token with valid admin credentials")
    public void theUserRequestsATokenWithValidAdminCredentials() {
        requestToken(ConfigReader.get("admin.username"), ConfigReader.get("admin.password"));
    }

    @When("the user requests a token with username {string} and password {string}")
    public void theUserRequestsATokenWith(String username, String password) {
        requestToken(username, password);
    }

    @Then("a token should be returned")
    public void aTokenShouldBeReturned() {
        AuthResponse body = context.getResponse().as(AuthResponse.class);
        assertThat(body.getToken()).isNotBlank();
    }

    @Then("no token should be returned and the reason should be {string}")
    public void noTokenShouldBeReturned(String expectedReason) {
        AuthResponse body = context.getResponse().as(AuthResponse.class);
        assertThat(body.getToken()).isNull();
        assertThat(body.getReason()).isEqualTo(expectedReason);
    }

    private void requestToken(String username, String password) {
        context.setResponse(
                given()
                    .spec(RequestSpecFactory.defaultSpec())
                    .body(new AuthRequest(username, password))
                .when()
                    .post(ConfigReader.get("endpoint.auth")));
    }
}
