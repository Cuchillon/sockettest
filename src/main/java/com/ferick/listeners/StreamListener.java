package com.ferick.listeners;

import com.ferick.environment.TestContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.BlockingDeque;

public class StreamListener extends WebSocketListener {

    private final TestContext context;
    private final BlockingDeque<JsonElement> eventList;

    public StreamListener(TestContext context, BlockingDeque<JsonElement> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        context.write("Socket is open");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        context.write("Socket has a message: " + text);
        var jsonElement = JsonParser.parseString(text);
        eventList.addLast(jsonElement);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        context.write("Socket has a message: " + bytes.hex());
        var text = String.valueOf(bytes);
        var jsonElement = JsonParser.parseString(text);
        eventList.addLast(jsonElement);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        webSocket.close(1000, null);
        context.write("Socket is closing: " + code + ", " + reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        context.writeError("Socket has failure: " + t.getMessage());
    }
}
