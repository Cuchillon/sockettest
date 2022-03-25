package com.ferick.tools.jsonutils.model;

import com.google.gson.JsonElement;

public class JsonObjectValue extends JsonValue {

    private final String parentNodeName;
    private final JsonElement parentElementForUpdating;

    public JsonObjectValue(JsonElement jsonElement, String parentNodeName, JsonElement parentElementForUpdating) {
        super(jsonElement);
        this.parentNodeName = parentNodeName;
        this.parentElementForUpdating = parentElementForUpdating;
    }

    public String getParentNodeName() {
        return parentNodeName;
    }

    public JsonElement getParentElementForUpdating() {
        return parentElementForUpdating;
    }
}
