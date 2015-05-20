package io.vertx.ext.apex.sse;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.apex.sse.impl.EventSourceImpl;

public interface EventSource {

    public static EventSource create(Vertx vertx, HttpClientOptions options) {
        return new EventSourceImpl(vertx, options);
    }

    @Fluent
    public EventSource connect(String path, Handler<AsyncResult<Void>> handler);

    @Fluent
    public EventSource close();

    @Fluent
    public EventSource connect(String path, String lastEventId, Handler<AsyncResult<Void>> handler);

    @Fluent
    public EventSource onMessage(Handler<String> messageHandler);

    @Fluent
    public EventSource onEvent(String eventName, Handler<String> handler);

    @Fluent
    public EventSource onClose(Handler<Void> handler);

    public String lastId();
}
