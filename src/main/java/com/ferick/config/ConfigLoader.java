package com.ferick.config;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigLoader {

    private static final String CONFIG_FILES_PATH = "src/test/resources/configs/";
    private static final String SYSTEM_CONFIG = "system-properties";
    private static final String COMMON_CONFIG = "common.yml";
    private static final String CONFIG_NAME_SUFFIX = "-config";

    private final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private final CombinedConfiguration configuration = loadSystemAndCommonConfigurations();

    public CombinedConfiguration getConfiguration() {
        return configuration;
    }

    public void addConfiguration(String configFileName) {
        addConfigurations(Collections.singletonList(configFileName));
    }

    public void addConfigurations(List<String> configFileNames) {
        var params = new Parameters();
        Map<String, FileBasedConfigurationBuilder<FileBasedConfiguration>> additionalProperties = new LinkedHashMap<>();

        configFileNames.forEach(configFileName -> {
            var file = new File(CONFIG_FILES_PATH + configFileName);
            var filenameExtension = configFileName.split("\\.")[1];
            FileBasedConfigurationBuilder<FileBasedConfiguration> configure =
                    new FileBasedConfigurationBuilder<FileBasedConfiguration>(
                            getFileBasedConfiguration(filenameExtension), null, true)
                            .configure(params.fileBased().setFile(file));
            additionalProperties.put(configFileName, configure);
        });

        additionalProperties.forEach((fileName, configure) -> {
            var configName = fileName + CONFIG_NAME_SUFFIX;
            try {
                configuration.addConfiguration(configure.getConfiguration(), configName);
            } catch (ConfigurationException e) {
                log.error(String.format("It cannot load configuration %s for testing", configName), e);
            }
        });
    }

    public void removeConfiguration(String configFileName) {
        configuration.removeConfiguration(configFileName + CONFIG_NAME_SUFFIX);
    }

    public void removeConfigurations(List<String> configFileNames) {
        configFileNames.forEach(configFileName ->
                configuration.removeConfiguration(configFileName + CONFIG_NAME_SUFFIX));
    }

    public void clearAdditionalConfigurations() {
        var configNames = configuration.getConfigurationNameList();
        for (String configName : configNames) {
            if (!(configName.equals(SYSTEM_CONFIG + CONFIG_NAME_SUFFIX) ||
                    configName.equals(COMMON_CONFIG + CONFIG_NAME_SUFFIX))) {
                configuration.removeConfiguration(configName);
            }
        }
    }

    public List<String> getAdditionalConfigurationNames() {
        var configNames = configuration.getConfigurationNameList();
        configNames.removeIf(configName ->
                (configName.equals(SYSTEM_CONFIG + CONFIG_NAME_SUFFIX) ||
                        configName.equals(COMMON_CONFIG + CONFIG_NAME_SUFFIX)));
        return configNames;
    }

    private CombinedConfiguration loadSystemAndCommonConfigurations() {
        var params = new Parameters();

        FileBasedConfigurationBuilder<FileBasedConfiguration> localProperties =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(YAMLConfiguration.class, null, true)
                        .configure(params.fileBased().setFile(new File(CONFIG_FILES_PATH + COMMON_CONFIG)));

        var configuration = new CombinedConfiguration();

        try {
            configuration.addConfiguration(new MapConfiguration(System.getProperties()), SYSTEM_CONFIG + CONFIG_NAME_SUFFIX);
            configuration.addConfiguration(localProperties.getConfiguration(), COMMON_CONFIG + CONFIG_NAME_SUFFIX);
        } catch (ConfigurationException e) {
            log.error("It cannot load configurations for testing", e);
            System.exit(1);
        }
        return configuration;
    }

    private Class<? extends FileBasedConfiguration> getFileBasedConfiguration(String filenameExtension) {
        return switch (filenameExtension) {
            case "properties" -> PropertiesConfiguration.class;
            case "yaml", "yml" -> YAMLConfiguration.class;
            case "json" -> JSONConfiguration.class;
            default -> throw new IllegalArgumentException(
                    "Wrong config file format. Config file format should be .properties, .yaml, .yml or .json");
        };
    }
}
