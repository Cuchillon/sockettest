package com.ferick.tools.jsonutils;

import com.ferick.tools.jsonutils.exceptions.JsonPathException;
import com.ferick.tools.jsonutils.model.ArrayItem;
import com.ferick.tools.jsonutils.model.JsonArrayValue;
import com.ferick.tools.jsonutils.model.JsonObjectValue;
import com.ferick.tools.jsonutils.model.JsonValue;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

class JsonHandler {

    JsonValue handleJsonValue(JsonElement rootElement, String[] jsonPath, Boolean updated) {
        if (jsonPath.length < 1) {
            throw new JsonPathException();
        } else if (jsonPath.length == 1 && jsonPath[0].equals("$")) {
            return new JsonValue(rootElement);
        }

        var nodeName = jsonPath[0];
        var arrayItem = checkJsonArray(nodeName);
        final var node = arrayItem.getNode();
        var isNodeArray = arrayItem.getNodeArray();
        int arrayIndex = arrayItem.getArrayIndex();

        if (jsonPath.length == 1) {
            if (rootElement.isJsonObject() && !isNodeArray) {
                var element = getJsonElementFromJsonObject(rootElement, node);
                return (updated) ? new JsonObjectValue(element, node, rootElement) : new JsonValue(element);
            } else if (rootElement.isJsonObject() && isNodeArray) {
                return getArrayForHandlingItem(rootElement, jsonPath, node, arrayIndex, updated);
            } else if (rootElement.isJsonArray() && isNodeArray) {
                var element = getJsonElementFromJsonArray(rootElement, arrayIndex);
                return (updated) ? new JsonArrayValue(element, arrayIndex, rootElement) : new JsonValue(element);
            } else {
                throw new JsonPathException();
            }
        } else {
            if (rootElement.isJsonObject() && !isNodeArray) {
                return parseJsonObject(rootElement, jsonPath, node, updated);
            } else if (rootElement.isJsonObject() && isNodeArray) {
                return getArrayForParsing(rootElement, jsonPath, node, arrayIndex, updated);
            } else if (rootElement.isJsonArray() && isNodeArray) {
                return parseJsonArray(rootElement, jsonPath, arrayIndex, updated);
            } else {
                throw new JsonPathException();
            }
        }
    }

    private JsonValue getArrayForParsing(JsonElement rootElement, String[] jsonPath, String node, int arrayIndex,
                                         Boolean updated) {
        var asJsonObject = rootElement.getAsJsonObject();
        var jsonElement = asJsonObject.entrySet().stream()
                .filter(entry -> entry.getKey().equals(node))
                .map(Map.Entry::getValue)
                .findFirst().orElseThrow(JsonPathException::new);
        jsonPath[0] = "[" + arrayIndex + "]";
        return handleJsonValue(jsonElement, jsonPath, updated);
    }

    private JsonValue parseJsonArray(JsonElement rootElement, String[] jsonPath, int arrayIndex, Boolean updated) {
        var asJsonArray = rootElement.getAsJsonArray();
        try {
            var jsonElement = Optional.ofNullable(asJsonArray.get(arrayIndex));
            if (jsonElement.isPresent()) {
                return handleJsonValue(jsonElement.get(), Arrays.copyOfRange(jsonPath, 1, jsonPath.length), updated);
            } else {
                throw new JsonPathException();
            }
        } catch (IndexOutOfBoundsException e) {
            throw new JsonPathException("JSON path is incorrect! " + e.getMessage());
        }
    }

    private JsonValue parseJsonObject(JsonElement rootElement, String[] jsonPath, String node, Boolean updated) {
        var asJsonObject = rootElement.getAsJsonObject();
        var jsonElement = asJsonObject.entrySet().stream()
                .filter(entry -> entry.getKey().equals(node))
                .map(Map.Entry::getValue)
                .findFirst().orElseThrow(JsonPathException::new);
        return handleJsonValue(jsonElement, Arrays.copyOfRange(jsonPath, 1, jsonPath.length), updated);
    }

    private JsonValue getArrayForHandlingItem(JsonElement rootElement, String[] jsonPath,
                                                String node, int arrayIndex, Boolean updated) {
        var asJsonObject = rootElement.getAsJsonObject();
        var jsonElement = asJsonObject.get(node);
        if (jsonElement != null && jsonElement.isJsonArray()) {
            jsonPath[0] = "[" + arrayIndex + "]";
            return handleJsonValue(jsonElement, jsonPath, updated);
        } else {
            throw new JsonPathException();
        }
    }

    private JsonElement getJsonElementFromJsonArray(JsonElement rootElement, int arrayIndex) {
        var asJsonArray = rootElement.getAsJsonArray();
        try {
            var jsonElement = Optional.ofNullable(asJsonArray.get(arrayIndex));
            return jsonElement.orElseThrow(JsonPathException::new);
        } catch (IndexOutOfBoundsException e) {
            throw new JsonPathException("JSON path is incorrect! " + e.getMessage());
        }
    }

    private JsonElement getJsonElementFromJsonObject(JsonElement rootElement, String node) {
        var asJsonObject = rootElement.getAsJsonObject();
        var jsonElement = Optional.ofNullable(asJsonObject.get(node));
        return jsonElement.orElseThrow(JsonPathException::new);
    }


    private ArrayItem checkJsonArray(String node) {
        var nodeName = node;
        var isNodeArray = false;
        int arrayIndex = -1;
        if (nodeName.endsWith("]")) {
            isNodeArray = true;
            arrayIndex = Integer.parseInt(StringUtils.substringBetween(nodeName, "[", "]"));
            nodeName = nodeName.replaceFirst("\\[\\d+]", "");
        }
        return new ArrayItem(nodeName, isNodeArray, arrayIndex);
    }
}
