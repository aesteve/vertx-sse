package io.vertx.ext.web.handler.sse;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public abstract class TestBase {

	protected final String TOKEN = "test";

	private final static String HOST = "localhost";
	private final static Integer PORT = 9000;

	protected Vertx vertx;
	protected SSEConnection connection;
	protected SSEHandler sseHandler;
	protected boolean closeHandlerCalled = false;

	private HttpServer server;
	private HttpClientOptions options;

	@BeforeEach
	public void createServer(VertxTestContext context) {
		closeHandlerCalled = false;
		vertx = Vertx.vertx();
		HttpServerOptions options = new HttpServerOptions();
		options.setHost(HOST);
		options.setPort(PORT);
		server = vertx.createHttpServer(options);
		Router router = Router.router(vertx);
		sseHandler = SSEHandler.create();
		sseHandler.connectHandler(connection -> {
			final HttpServerRequest request = connection.request();
			final String token = request.getParam("token");
			if (token == null) {
				connection.reject(401);
			} else if (!TOKEN.equals(token)) {
				connection.reject(403);
			} else {
				this.connection = connection; // accept
			}
		});
		sseHandler.closeHandler(connection -> {
			closeHandlerCalled = true;
			if (this.connection != null) {
				this.connection = null;
			}
		});
		router.get("/sse").handler(sseHandler);
		addBridge(router);
		server.requestHandler(router);
		server.listen(context.completing());
	}

	@AfterEach
	public void shutDown(VertxTestContext context) {
		connection = null;
		sseHandler = null;
		closeHandlerCalled = false;
		if (vertx != null) {
			vertx.close(context.completing()); // will shut down the server
		} else {
			context.completeNow();
		}
	}

	protected void addBridge(Router router) {}

	EventSource eventSource() {
		return EventSource.create(vertx, clientOptions());
	}

	HttpClient client() {
		return vertx.createHttpClient(clientOptions());
	}

	private HttpClientOptions clientOptions() {
		if (options == null) {
			options = new HttpClientOptions();
			options.setDefaultHost(HOST);
			options.setDefaultPort(PORT);
		}
		return options;
	}

}
