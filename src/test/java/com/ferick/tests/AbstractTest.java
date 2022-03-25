package com.ferick.tests;

import com.ferick.environment.TestContext;
import com.ferick.extensions.EnvironmentExtension;
import com.ferick.helpers.BaseMethods;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

@ExtendWith(EnvironmentExtension.class)
public abstract class AbstractTest {

    protected static final String PET_TEMPLATE_PATH = "pet/pet-template.txt";
    protected static final String PET_ID_VAR = "petId";

    @Inject
    protected TestContext context;

    protected BaseMethods baseMethods() {
        return context.helpers().baseMethods();
    }
}
