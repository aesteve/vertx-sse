package io.vertx.ext.apex.sse.impl;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.sse.SSEConnection;
import io.vertx.ext.apex.sse.handlers.impl.SSEHandlerImpl;

public class SSEConnectionImpl implements SSEConnection {

    private RoutingContext context;
    private SSEHandlerImpl handler;

    public SSEConnectionImpl(RoutingContext context, SSEHandlerImpl handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public SSEConnection data(Buffer buffer) {
        HttpServerResponse response = context.response();
        response.setChunked(true);
        response.write("data: " + buffer.toString("UTF-8") + "\n\n");
        return this;
    }

    @Override
    public SSEConnection event(Buffer buffer) {
        HttpServerResponse response = context.response();
        response.write("event: " + buffer.toString("UTF-8") + "\n\n");
        return this;
    }

    @Override
    public SSEConnection close(Buffer buffer) {
        handler.getCloseHandlers().forEach(closeHandler -> {
            closeHandler.handle(this);
        });
        context.response().end();
        return this;
    }

}
