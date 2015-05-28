package io.vertx.ext.web.handler.sse;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sse.SSEConnection;
import io.vertx.ext.web.handler.sse.SSEHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestVerticle extends AbstractVerticle {

    public static final Integer PORT = 9000;
    public static final String HOST = "localhost";
    private Map<SSEConnection, Long> timersPerConnection;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        timersPerConnection = new HashMap<SSEConnection, Long>();
    }

    @Override
    public void start(Future<Void> future) {
        HttpServer server = vertx.createHttpServer(serverOptions());
        Router router = Router.router(vertx);
        router.get("/index").handler(context -> {
            context.response().sendFile("src/test/resources/index.html");
        }); // for integration tests in the browser
        SSEHandler pingSSEHandler = SSEHandler.create();
        pingSSEHandler.connectHandler(sseConnection -> {
            Long timerId = vertx.setPeriodic(1000, tId -> {
                sseConnection.data("ping ! " + new Date().toString());
            });
            timersPerConnection.put(sseConnection, timerId);
            // sseConnection.reject(403);
        });
        pingSSEHandler.closeHandler(sseConnection -> {
            Long timerId = timersPerConnection.get(sseConnection);
            if (timerId != null) {
                vertx.cancelTimer(timerId);
            }
        });
        router.route("/sse").handler(pingSSEHandler);
        server.requestHandler(router::accept);
        server.listen(result -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> future) {
        future.complete();
    }

    private HttpServerOptions serverOptions() {
        HttpServerOptions options = new HttpServerOptions();
        options.setHost(HOST);
        options.setPort(PORT);
        return options;
    }

}
