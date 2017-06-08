package io.vertx.ext.web.handler.sse;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.handler.sse.impl.EventBusSSEBridgeImpl;

import java.util.function.Function;

public interface EventBusSSEBridge extends SSEHandler {

    static EventBusSSEBridge create() {
        return new EventBusSSEBridgeImpl();
    }

    /**
     * Defines the mapping between the incoming HttpServerRequest and the EventBus address
     *
     * @param mapper the function returning the EventBus address to subscribe to according to the incoming HTTP request
     * @return a reference to this, so that the API can be used fluently
     */
    @Fluent
    EventBusSSEBridge mapping(Function<HttpServerRequest, String> mapper);

}
