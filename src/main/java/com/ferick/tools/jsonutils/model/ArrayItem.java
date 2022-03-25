package com.ferick.tools.jsonutils.model;

public class ArrayItem {

    private final String node;
    private final Boolean isNodeArray;
    private final Integer arrayIndex;

    public ArrayItem(String node, Boolean isNodeArray, Integer arrayIndex) {
        this.node = node;
        this.isNodeArray = isNodeArray;
        this.arrayIndex = arrayIndex;
    }

    public String getNode() {
        return node;
    }

    public Boolean getNodeArray() {
        return isNodeArray;
    }

    public Integer getArrayIndex() {
        return arrayIndex;
    }
}
