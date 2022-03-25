package com.ferick.reporting;

import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllureLogger {

    private final Logger log;

    public AllureLogger(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
    }

    @Step("{0}")
    public void info(String message) {
        log.info(message);
    }

    @Step("ERROR: {0}")
    public void error(String message) {
        log.error(message);
    }
}
