package com.ferick.environment;

import com.ferick.config.ConfigLoader;
import com.ferick.config.ConfigUtils;
import com.ferick.config.PropertyLoader;
import com.ferick.helpers.HelperManager;
import com.ferick.reporting.AllureLogger;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestContext {

    /**
     * Loader for system, common and custom test configurations loaded from scripts
     */
    private final ConfigLoader configLoader = new ConfigLoader();

    /**
     * Loader of properties from configurations
     */
    private final PropertyLoader propertyLoader = new PropertyLoader(configLoader.getConfiguration());

    private final ExtensionContext.Store store;
    private final AllureLogger logger;
    private HelperManager helpers;

    public TestContext(ExtensionContext.Store store, AllureLogger logger) {
        this.store = store;
        this.logger = logger;
    }

    public CombinedConfiguration getConfiguration() {
        return configLoader.getConfiguration();
    }

    public void addConfiguration(String configFileName) {
        configLoader.addConfiguration(configFileName);
    }

    public void addConfigurations(List<String> configFileNames) {
        configLoader.addConfigurations(configFileNames);
    }

    public void removeConfiguration(String configFileName) {
        configLoader.removeConfiguration(configFileName);
    }

    public void removeConfigurations(List<String> configFileNames) {
        configLoader.removeConfigurations(configFileNames);
    }

    public void clearAdditionalConfigurations() {
        configLoader.clearAdditionalConfigurations();
    }

    public List<String> getAdditionalConfigurationNames() {
        return configLoader.getAdditionalConfigurationNames();
    }

    public List<String> getRootConfigNames() {
        var configNames = getAdditionalConfigurationNames();
        return configNames.stream().map(ConfigUtils::parseConfigFileName).collect(Collectors.toList());
    }

    public PropertyLoader properties() {
        return propertyLoader;
    }

    public HelperManager helpers() {
        return (helpers == null) ? helpers = new HelperManager(this) : helpers;
    }

    public void write(String message) {
        logger.info(message);
    }

    public void writeError(String message) {
        logger.error(message);
    }

    public void setVariable(String key, Object value) {
        store.put(key, value);
    }

    public Optional<Object> getVariable(String key) {
        var obj = store.get(key);
        return (obj != null) ? Optional.of(obj) : Optional.empty();
    }

    public String getStringVariable(String key) {
        return (String) getVariable(key).orElse(null);
    }

    public Integer getIntVariable(String key) {
        return (Integer) getVariable(key).orElse(null);
    }

    public Double getDoubleVariable(String key) {
        return (Double) getVariable(key).orElse(null);
    }

    public <T> T getVariable(String key, Class<T> clazz) {
        var optional = getVariable(key);
        if (optional.isPresent()) {
            var obj = optional.get();
            if (clazz.isAssignableFrom(obj.getClass())) {
                return clazz.cast(obj);
            } else {
                throw new ClassCastException("Variable type does not equal to the type passed in argument!");
            }
        }
        return null;
    }
}
