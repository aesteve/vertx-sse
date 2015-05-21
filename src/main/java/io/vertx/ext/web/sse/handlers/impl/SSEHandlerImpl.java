package io.vertx.ext.web.sse.handlers.impl;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.sse.SSEConnection;
import io.vertx.ext.web.sse.handlers.SSEHandler;

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
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        response.setChunked(true);
        SSEConnection connection = SSEConnection.create(context);
        String accept = request.getHeader("Accept");
        if (accept == null || accept.indexOf("text/event-stream") == -1) {
            connection.reject(406, "Not acceptable");
            return;
        }
        response.closeHandler(voidz -> {
            closeHandlers.forEach(closeHandler -> {
                closeHandler.handle(connection);
            });
        });
        response.headers().add("Content-Type", "text/event-stream");
        response.headers().add("Cache-Control", "no-cache");
        response.headers().add("Connection", "keep-alive");
        connectHandlers.forEach(handler -> {
            handler.handle(connection);
        });
        if (!connection.rejected()) {
            response.setStatusCode(200);
            response.setChunked(true);
            response.write(""); // FIXME : how to trigger the response handler with no data ?
        }
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
}
