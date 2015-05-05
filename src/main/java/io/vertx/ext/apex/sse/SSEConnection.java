package io.vertx.ext.apex.sse;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.sse.handlers.impl.SSEHandlerImpl;
import io.vertx.ext.apex.sse.impl.SSEConnectionImpl;

public interface SSEConnection {

    static SSEConnection create(RoutingContext context, SSEHandlerImpl handler) {
        return new SSEConnectionImpl(context, handler);
    }

    public SSEConnection data(Buffer buffer);

    public SSEConnection event(Buffer buffer);

    public SSEConnection close(Buffer buffer);
}
