package com.raykurt.api.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/** Runs only the authentication features. */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/auth")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.raykurt.api")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/auth.html, json:target/cucumber-reports/auth.json")
public class AuthRunner {
}
