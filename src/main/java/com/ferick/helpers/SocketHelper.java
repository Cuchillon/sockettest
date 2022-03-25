package com.ferick.helpers;

import com.ferick.environment.TestContext;
import com.ferick.listeners.StreamListener;
import com.google.gson.JsonElement;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SocketHelper {

    private static final long DEFAULT_TIMEOUT = 10000L;

    private final TestContext context;
    private final HelperManager manager;
    private final OkHttpClient client;
    private final BlockingDeque<JsonElement> events;
    private final StreamListener listener;

    public SocketHelper(TestContext context, HelperManager manager) {
        this.context = context;
        this.manager = manager;
        this.client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS).build();
        this.events = new LinkedBlockingDeque<>();
        this.listener = new StreamListener(context, events);
    }

    public WebSocket getSocket(String url) {
        var request = new Request.Builder().url(url).build();
        return client.newWebSocket(request, listener);
    }

    public Number getNumberFromEvent(String path) {
        var jsonPath = manager.baseMethods().jsonPath();
        return jsonPath.getNumberValue(getEvent(), path);
    }

    public JsonElement getEventByCondition(Function<JsonElement, Boolean> condition) {
        var jsonElement = getEvent();
        var start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < DEFAULT_TIMEOUT && !condition.apply(jsonElement)) {
            jsonElement = getEvent();
        }
        if (System.currentTimeMillis() - start < DEFAULT_TIMEOUT) {
            return jsonElement;
        } else {
            throw new AssertionError("Event satisfying the condition not found");
        }
    }

    private JsonElement getEvent() {
        JsonElement jsonElement = null;
        try {
            jsonElement = events.pollFirst(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            context.writeError("Getting event was interrupted");
            Thread.currentThread().interrupt();
        }
        if (jsonElement != null) {
            return jsonElement;
        } else {
            throw new AssertionError("No event was found during default timeout");
        }
    }
}
