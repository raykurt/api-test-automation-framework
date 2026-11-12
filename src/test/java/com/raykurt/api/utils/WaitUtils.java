package com.raykurt.api.utils;

import com.raykurt.api.config.ConfigReader;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;

/**
 * Polling waits for results that are not available immediately
 * (asynchronous processing, eventual consistency). Never Thread.sleep.
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    /** Polls the condition until it is true or the configured timeout is reached. */
    public static void waitUntil(String description, Callable<Boolean> condition) {
        await(description)
                .atMost(Duration.ofSeconds(ConfigReader.getInt("wait.timeout.seconds")))
                .pollInterval(Duration.ofSeconds(ConfigReader.getInt("wait.poll.seconds")))
                .ignoreExceptions()
                .until(condition);
    }
}
