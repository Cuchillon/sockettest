package com.ferick.helpers;

import com.ferick.environment.TestContext;
import com.ferick.tools.jsonutils.JsonPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseMethods {

    private static final String BASE_URL_SYSTEM_PATH = "common.baseUrl";
    private static final String BASE_URL_CONFIG_PATH = "%s.baseUrl";
    private static final Path RESOURCE_PATH = Paths.get("src/test/resources");

    private final TestContext context;
    private JsonPath jsonPath;

    public BaseMethods(TestContext context) {
        this.context = context;
    }

    public JsonPath jsonPath() {
        return (jsonPath == null) ? jsonPath = new JsonPath() : jsonPath;
    }

    public String getBaseUrl() {
        return context.properties().loadPropertyOrDefault(BASE_URL_SYSTEM_PATH,
                context.getRootConfigNames().stream()
                        .map(rootName -> String.format(BASE_URL_CONFIG_PATH, rootName))
                        .map(context.properties()::loadStringProperty)
                        .findFirst().orElse("http://localhost"));
    }

    private String readStringFromFile(Path path) {
        try {
            return Files.readString(RESOURCE_PATH.resolve(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file " + path, e);
        }
    }
}
