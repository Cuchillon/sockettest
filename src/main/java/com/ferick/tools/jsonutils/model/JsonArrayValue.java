package com.ferick.tools.jsonutils.model;

import com.google.gson.JsonElement;

public class JsonArrayValue extends JsonValue {

    private final Integer parentArrayIndex;
    private final JsonElement parentElementForUpdating;

    public JsonArrayValue(JsonElement jsonElement, Integer parentArrayIndex, JsonElement parentElementForUpdating) {
        super(jsonElement);
        this.parentArrayIndex = parentArrayIndex;
        this.parentElementForUpdating = parentElementForUpdating;
    }

    public Integer getParentArrayIndex() {
        return parentArrayIndex;
    }

    public JsonElement getParentElementForUpdating() {
        return parentElementForUpdating;
    }
}
