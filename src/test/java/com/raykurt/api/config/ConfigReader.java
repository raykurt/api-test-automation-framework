package com.raykurt.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads configuration in three layers (later wins):
 * 1. config.properties             shared values
 * 2. config-&lt;env&gt;.properties   environment values, selected with -Denv=ci
 * 3. system properties             single overrides, e.g. -Dbase.url=http://localhost:3001
 */
public final class ConfigReader {

    private static final String BASE_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    public static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing configuration value for key: " + key);
        }
        return value;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key).trim());
    }

    private static Properties load() {
        Properties properties = new Properties();
        loadInto(properties, BASE_FILE, true);

        String env = System.getProperty("env");
        if (env != null && !env.isBlank()) {
            loadInto(properties, "config-" + env.trim() + ".properties", true);
        }
        return properties;
    }

    private static void loadInto(Properties properties, String fileName, boolean required) {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                if (required) {
                    throw new IllegalStateException(fileName + " was not found on the classpath");
                }
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + fileName, e);
        }
    }
}
