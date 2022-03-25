package com.ferick.extensions;

import com.ferick.config.Configuration;
import com.ferick.environment.TestContext;
import com.ferick.reporting.AllureLogger;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.*;

public class EnvironmentExtension implements TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback {

    private TestContext testContext;

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        var store = context.getStore(ExtensionContext.Namespace.create(getClass(), testInstance));
        var logger = new AllureLogger(testInstance.getClass());
        testContext = new TestContext(store, logger);
        injectModulesContext(testInstance);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        var fileNames = checkAdditionalConfigurations(context.getRequiredTestInstance());
        testContext.write("Test configurations loaded from files " + fileNames);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        testContext.clearAdditionalConfigurations();
    }

    private void injectModulesContext(Object testInstance)
            throws IllegalAccessException {
        var fields = new ArrayList<Field>();
        aggregateContextFields(testInstance.getClass(), fields);
        if (fields.size() == 1) {
            var envField = fields.get(0);
            envField.setAccessible(true);
            envField.set(testInstance, testContext);
        } else if (fields.size() > 1) {
            throw new IllegalStateException(
                    "There are more than one test context fields with @Inject annotation!");
        } else {
            throw new IllegalStateException("Test context field with @Inject annotation not found!");
        }
    }

    private LinkedHashSet<String> checkAdditionalConfigurations(Object testInstance) {
        var fileNames = new LinkedHashSet<String>();
        aggregateAdditionalConfigurations(testInstance.getClass(), fileNames);
        testContext.addConfigurations(List.copyOf(fileNames));
        return fileNames;
    }

    private void aggregateContextFields(Class<?> clazz, List<Field> fields) {
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class)
                        && field.getType().isAssignableFrom(TestContext.class))
                .forEach(fields::add);
        if (clazz.getSuperclass() != null) {
            aggregateContextFields(clazz.getSuperclass(), fields);
        }
    }

    private void aggregateAdditionalConfigurations(Class<?> clazz, Set<String> fileNames) {
        if (clazz.isAnnotationPresent(Configuration.class)) {
            String[] values = clazz.getDeclaredAnnotation(Configuration.class).value();
            fileNames.addAll(Arrays.asList(values));
        }
        if (clazz.getSuperclass() != null) {
            aggregateAdditionalConfigurations(clazz.getSuperclass(), fileNames);
        }
    }
}
