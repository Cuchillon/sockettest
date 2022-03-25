package com.ferick.tools.jsonutils;

import com.ferick.tools.jsonutils.exceptions.JsonElementTypeException;
import com.ferick.tools.jsonutils.model.JsonArrayValue;
import com.ferick.tools.jsonutils.model.JsonObjectValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Optional;
import java.util.function.Function;

public class JsonPath {

    private final JsonHandler handler = new JsonHandler();

    public void updateStringValue(JsonElement rootElement, String jsonPath, String value) {
        updateJsonValue(rootElement, jsonPath, new JsonPrimitive(value));
    }

    public void updateNumberValue(JsonElement rootElement, String jsonPath, Number value) {
        updateJsonValue(rootElement, jsonPath, new JsonPrimitive(value));
    }

    public void updateBooleanValue(JsonElement rootElement, String jsonPath, Boolean value) {
        updateJsonValue(rootElement, jsonPath, new JsonPrimitive(value));
    }

    public void updateJsonValue(JsonElement rootElement, String jsonPath, JsonElement value) {
        var pathNodeList = jsonPath.split("\\.");
        updateJsonValue(rootElement, pathNodeList, value);
    }

    public void setJsonElementToArray(JsonElement rootElement, String jsonPath, JsonElement value) {
        var pathNodeList = jsonPath.split("\\.");
        setJsonElementToArray(rootElement, pathNodeList, value);
    }

    public void setJsonElementToObject(JsonElement rootElement, String jsonPath, String name, JsonElement value) {
        var pathNodeList = jsonPath.split("\\.");
        setJsonElementToObject(rootElement, pathNodeList, name, value);
    }

    public String getStringValue(JsonElement rootElement, String jsonPath) {
        return extractValue(rootElement, jsonPath, (value) -> {
            if (value.isString()) {
                return value.getAsString();
            } else {
                throw new JsonElementTypeException("JSON value is not a string!");
            }
        });
    }

    public Number getNumberValue(JsonElement rootElement, String jsonPath) {
        return extractValue(rootElement, jsonPath, (value) -> {
            if (value.isNumber()) {
                return value.getAsNumber();
            } else {
                throw new JsonElementTypeException("JSON value is not a number!");
            }
        });
    }

    public Boolean getBooleanValue(JsonElement rootElement, String jsonPath) {
        return extractValue(rootElement, jsonPath, (value) -> {
            if (value.isBoolean()) {
                return value.getAsBoolean();
            } else {
                throw new JsonElementTypeException("JSON value is not a boolean!");
            }
        });
    }

    public Optional<JsonElement> getJsonValue(JsonElement rootElement, String jsonPath) {
        var pathNodeList = jsonPath.split("\\.");
        return getJsonValue(rootElement, pathNodeList);
    }

    private Optional<JsonElement> getJsonValue(JsonElement rootElement, String[] jsonPath) {
        return Optional.ofNullable(handler.handleJsonValue(rootElement, jsonPath, Boolean.FALSE).getJsonElement());
    }

    private void updateJsonValue(JsonElement rootElement, String[] jsonPath, JsonElement value) {
        var jsonValue = handler.handleJsonValue(rootElement, jsonPath, Boolean.TRUE);
        var jsonElement = jsonValue.getJsonElement();
        if (jsonElement.isJsonPrimitive()) {
            if (jsonValue instanceof JsonObjectValue) {
                ((JsonObjectValue) jsonValue).getParentElementForUpdating().getAsJsonObject()
                        .add(((JsonObjectValue) jsonValue).getParentNodeName(), value);
            } else if (jsonValue instanceof JsonArrayValue) {
                ((JsonArrayValue) jsonValue).getParentElementForUpdating().getAsJsonArray()
                        .set(((JsonArrayValue) jsonValue).getParentArrayIndex(), value);
            } else {
                throw new JsonElementTypeException("JSON value is neither JSON object nor JSON array!");
            }
        } else {
            throw new JsonElementTypeException("JSON element is not primitive!");
        }
    }

    private void setJsonElementToArray(JsonElement rootElement, String[] jsonPath, JsonElement value) {
        var jsonElement = handler.handleJsonValue(rootElement, jsonPath, Boolean.FALSE).getJsonElement();
        if (jsonElement.isJsonArray()) {
            jsonElement.getAsJsonArray().add(value);
        } else {
            throw new JsonElementTypeException("Parent JSON element is not array!");
        }
    }

    private void setJsonElementToObject(JsonElement rootElement, String[] jsonPath, String name, JsonElement value) {
        var jsonElement = handler.handleJsonValue(rootElement, jsonPath, Boolean.FALSE).getJsonElement();
        if (jsonElement.isJsonObject()) {
            jsonElement.getAsJsonObject().add(name, value);
        } else {
            throw new JsonElementTypeException("JSON element is not object!");
        }
    }

    private <T> T extractValue(JsonElement rootElement, String jsonPath, Function<JsonPrimitive, T> function) {
        var optional = getJsonValue(rootElement, jsonPath);
        if (optional.isPresent()) {
            if (optional.get().isJsonPrimitive()) {
                var value = optional.get().getAsJsonPrimitive();
                return function.apply(value);
            } else {
                throw new JsonElementTypeException("JSON element is not primitive!");
            }
        } else {
            throw new NullPointerException(String.format("JSON element with path %s not found!", jsonPath));
        }
    }
}
