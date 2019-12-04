package io.vertx.server;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.sse.SSEConnection;
import io.vertx.ext.web.handler.sse.SSEHandler;
/**
 * Cros Server Example
 * @author lxiaodao
 *
 */
public class Server {
	private final static String HOST = "localhost";
	private final static Integer PORT = 9000;

	private SSEConnection connection;
	private Vertx vertx;

	private SSEHandler sseHandler;
	private HttpServer server;
	
	public void stop() {
		if(null!=this.connection) {
			this.connection.close();
		}
		
		if(null!=this.server) {
		this.server.close();
		}
		System.out.println("============Server stopped==================");
	}

	public void start() {

		vertx = Vertx.vertx();
		HttpServerOptions options = new HttpServerOptions();
		options.setHost(HOST);
		options.setPort(PORT);

		server = vertx.createHttpServer(options);

		Router router = Router.router(vertx);
		sseHandler = SSEHandler.create();

		sseHandler.connectHandler(connection -> {
			final Long timerId = vertx.setPeriodic(1000, tId -> {

				connection.data("ping ! " + new Date().toString());
			});
			this.connection = connection;

		});
		/**
		 * sseHandler.closeHandler(connection -> { if (this.connection != null) {
		 * this.connection = null; } });
		 */

		// --------------------------------------//
		Set<String> allowedHeaders = new HashSet<>();
		allowedHeaders.add("x-requested-with");
		allowedHeaders.add("Access-Control-Allow-Origin");
		allowedHeaders.add("origin");
		allowedHeaders.add("Content-Type");
		allowedHeaders.add("accept");
		allowedHeaders.add("X-PINGARUNER");

		Set<HttpMethod> allowedMethods = new HashSet<>();
		allowedMethods.add(HttpMethod.GET);
		allowedMethods.add(HttpMethod.POST);
		allowedMethods.add(HttpMethod.OPTIONS);
		/*
		 * these methods aren't necessary for this sample, but you may need them for
		 * your projects
		 */
		allowedMethods.add(HttpMethod.DELETE);
		allowedMethods.add(HttpMethod.PATCH);
		allowedMethods.add(HttpMethod.PUT);

		router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

		router.get("/sse").handler(sseHandler);
		server.requestHandler(router);
		server.listen();

		System.out.println("============Server started==================");
	}

	public static void main(String[] arr) {

		new Server().start();
	}

}
