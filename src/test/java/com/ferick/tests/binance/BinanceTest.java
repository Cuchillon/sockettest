package com.ferick.tests.binance;

import com.ferick.config.Configuration;
import com.ferick.tests.AbstractTest;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("binance")
@Configuration("binance.yml")
public class BinanceTest extends AbstractTest {

    private static final String U_FIELD_PATH = "data.u";
    private static final String A_FIELD_PATH = "data.a";
    private static final String B_FIELD_PATH = "data.b";
    private static final String FIRST_VALUE_PATH = "[0].[0]";

    @BeforeEach
    public void setup() {
        var url = String.format("%s%s?%s", baseMethods().getBaseUrl(),
                context.properties().loadStringProperty("binance.path"),
                context.properties().loadStringProperty("binance.params"));
        context.helpers().socketHelper().getSocket(url);
    }

    @Test
    @DisplayName("Checking 'u' field")
    public void checkUField() {
        var firstEvent = context.helpers().socketHelper().getNumberFromEvent(U_FIELD_PATH);
        var secondEvent = context.helpers().socketHelper().getNumberFromEvent(U_FIELD_PATH);
        assertTrue(secondEvent.longValue() > firstEvent.longValue(),
                "Second 'u' value must be greater than first one");
    }

    @Test
    @DisplayName("Checking 'a' and 'b' fields")
    public void checkABFields() {
        var jsonPath = baseMethods().jsonPath();
        Function<JsonElement, Boolean> condition = (jsonElement) ->
                jsonPath.getJsonValue(jsonElement, A_FIELD_PATH).isPresent()
                        && jsonPath.getJsonValue(jsonElement, B_FIELD_PATH).isPresent();
        var event = context.helpers().socketHelper().getEventByCondition(condition);
        var aValue = new BigDecimal(jsonPath.getStringValue(event, A_FIELD_PATH + FIRST_VALUE_PATH));
        var bValue = new BigDecimal(jsonPath.getStringValue(event, B_FIELD_PATH + FIRST_VALUE_PATH));
        assertEquals(-1, bValue.compareTo(aValue),
                "'b' value must be less than 'a' value");
    }
}
