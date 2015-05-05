package io.vertx.ext.apex.sse.handlers.impl;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.sse.SSEConnection;
import io.vertx.ext.apex.sse.handlers.SSEHandler;

import java.util.ArrayList;
import java.util.List;

public class SSEHandlerImpl implements SSEHandler {

    private List<Handler<SSEConnection>> connectHandlers;
    private List<Handler<SSEConnection>> closeHandlers;

    public SSEHandlerImpl() {
        connectHandlers = new ArrayList<Handler<SSEConnection>>();
        closeHandlers = new ArrayList<Handler<SSEConnection>>();
    }

    @Override
    public void handle(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.headers().add("Content-Type", "text/event-stream");
        SSEConnection connection = SSEConnection.create(context, this);
        connectHandlers.forEach(handler -> {
            handler.handle(connection);
        });
    }

    @Override
    public SSEHandler connectHandler(Handler<SSEConnection> handler) {
        connectHandlers.add(handler);
        return this;
    }

    @Override
    public SSEHandler closeHandler(Handler<SSEConnection> handler) {
        closeHandlers.add(handler);
        return this;
    }

    public List<Handler<SSEConnection>> getCloseHandlers() {
        return closeHandlers;
    }
}
