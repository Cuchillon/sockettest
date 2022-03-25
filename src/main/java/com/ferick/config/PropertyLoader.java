package com.ferick.config;

import org.apache.commons.configuration2.CombinedConfiguration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyLoader {

    private final CombinedConfiguration configuration;

    public PropertyLoader(CombinedConfiguration configuration) {
        this.configuration = configuration;
    }

    public String loadStringProperty(String propertyName) {
        return configuration.getString(propertyName);
    }

    public int loadIntProperty(String propertyName) {
        return configuration.getInt(propertyName);
    }

    public Boolean loadBooleanProperty(String propertyName) {
        return configuration.get(Boolean.class, propertyName);
    }

    public List<String> loadStringListProperty(String propertyName) {
        return configuration.getList(String.class, propertyName);
    }

    public List<Integer> loadIntListProperty(String propertyName) {
        return configuration.getList(Integer.class, propertyName);
    }

    public <T> T loadProperty(Class<T> clazz, String propertyName) {
        return configuration.get(clazz, propertyName);
    }

    /**
     * Key-value list should be put in config file like: ["key1=value1", "key2=value2"]
     */
    public Map<String, String> loadMapProperty(String propertyName) {
        return configuration.getProperties(propertyName).entrySet().stream()
                .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (String) entry.getValue()));
    }

    public String loadPropertyOrDefault(String propertyName, String defaultValue) {
        return configuration.getString(propertyName, defaultValue);
    }

    public Integer loadPropertyOrDefault(String propertyName, Integer defaultValue) {
        return configuration.getInteger(propertyName, defaultValue);
    }

    public Boolean loadPropertyOrDefault(String propertyName, Boolean defaultValue) {
        return configuration.get(Boolean.class, propertyName, defaultValue);
    }

    public List<String> loadPropertyOrDefault(String propertyName, List<String> defaultValue) {
        return configuration.getList(String.class, propertyName, defaultValue);
    }

    public <T> T loadPropertyOrDefault(Class<T> clazz, String propertyName, T defaultValue) {
        return configuration.get(clazz, propertyName, defaultValue);
    }

    public String getPropertyOrValue(String propertyName) {
        return loadPropertyOrDefault(propertyName, propertyName);
    }
}
