package io.vertx.ext.apex.sse;

import java.util.List;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.sse.impl.SSEConnectionImpl;

@VertxGen
public interface SSEConnection {

    static SSEConnection create(RoutingContext context) {
        return new SSEConnectionImpl(context);
    }

    @Fluent
    public SSEConnection reject(int code);

    @Fluent
    public SSEConnection reject(int code, String reason);

    @Fluent
    public SSEConnection comment(String comment);

    @Fluent
    public SSEConnection retry(Long delay, List<String> data);

    @Fluent
    public SSEConnection retry(Long delay, String data);

    @Fluent
    public SSEConnection data(List<String> data);

    @Fluent
    public SSEConnection data(String data);

    @Fluent
    public SSEConnection event(String eventName, List<String> data);

    @Fluent
    public SSEConnection event(String eventName, String data);

    @Fluent
    public SSEConnection id(String id, List<String> data);

    @Fluent
    public SSEConnection id(String id, String data);

    @Fluent
    public SSEConnection close();

    public boolean rejected();

    public HttpServerRequest request();
}