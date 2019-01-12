package com.github.aesteve.vertx.sse.examples.iss;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sse.SSEHandler;

public class ISSVerticle extends AbstractVerticle {

    public final static int PORT = 9001;

    private final static Logger LOG = LoggerFactory.getLogger(ISSVerticle.class.getName());
    private final static String EB_ADDRESS = "iss-position";

    private HttpServer server;
    private StaticHandler staticFiles = StaticHandler.create();
    private SSEHandler sse = SSEHandler.create();
    private Long timerId;
    private HttpClient client;


    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        client = vertx.createHttpClient(new HttpClientOptions().setDefaultHost("api.open-notify.org"));
    }

    @Override
    public void start(Future<Void> future) {
        server = vertx.createHttpServer();
        var router = Router.router(vertx);
        router.get("/").handler(rc -> rc.reroute("/static/index.html"));
        router.get("/static/*").handler(staticFiles);
        router.get("/iss/position").handler(sse);

        sse.connectHandler(connection -> {
            connection.forward(EB_ADDRESS);
        });

        vertx.setPeriodic(10000, this::fetchISSPosition);
        server.requestHandler(router);
        server.listen(PORT, future.<HttpServer>map(v -> null).completer());
    }

    private void fetchISSPosition(Long timerId) {
        this.timerId = timerId;
        client.getNow("/iss-now.json", resp -> {
            if (resp.statusCode() != 200) {
                LOG.error("Could not fetch ISS position {}", resp.statusCode());
                return;
            }
            resp.bodyHandler(buff -> {
                var json = buff.toJsonObject();
                if (!"success".equals(json.getString("message"))) {
                    LOG.error("Could not fetch ISS position {}", json.toString());
                    return;
                }
                var position = json.getJsonObject("iss_position");
                vertx.eventBus().publish(EB_ADDRESS, position);
            });
        });
    }

    @Override
    public void stop(Future<Void> future) {
        if (timerId != null) {
            vertx.cancelTimer(timerId);
        }
        if (server == null) {
            future.complete();
            return;
        }
        server.close(future.completer());
    }

}
