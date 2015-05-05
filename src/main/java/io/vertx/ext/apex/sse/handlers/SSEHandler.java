package io.vertx.ext.apex.sse.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.sse.SSEConnection;
import io.vertx.ext.apex.sse.handlers.impl.SSEHandlerImpl;

public interface SSEHandler extends Handler<RoutingContext> {

    static SSEHandler create() {
        return new SSEHandlerImpl();
    }

    public SSEHandler connectHandler(Handler<SSEConnection> connection);

    public SSEHandler closeHandler(Handler<SSEConnection> connection);
}
