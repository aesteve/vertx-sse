package io.vertx.ext.web.handler.sse;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.Router;

import org.junit.After;
import org.junit.Before;

public class TestBase {

	protected final String TOKEN = "test";

	private final static String HOST = "localhost";
	private final static Integer PORT = 9000;

	protected Vertx vertx;
	protected SSEConnection connection;
	protected SSEHandler sseHandler;

	private HttpServer server;
	private HttpClientOptions options;

	@Before
	public void createServer(TestContext context) {
		vertx = Vertx.vertx();
		HttpServerOptions options = new HttpServerOptions();
		options.setHost(HOST);
		options.setPort(PORT);
		server = vertx.createHttpServer(options);
		Router router = Router.router(vertx);
		sseHandler = SSEHandler.create();
		sseHandler.connectHandler(connection -> {
			HttpServerRequest request = connection.request();
			String token = request.getParam("token");
			if (token == null) {
				connection.reject(401);
			} else if (!TOKEN.equals(token)) {
				connection.reject(403);
			} else {
				this.connection = connection; // accept
			}
		});
		sseHandler.closeHandler(connection -> {
			if (this.connection != null) {
				this.connection = null;
			}
		});
		router.get("/sse").handler(sseHandler);
		server.requestHandler(router::accept);
		server.listen(context.asyncAssertSuccess());
	}

	@After
	public void shutDown(TestContext context) {
		connection = null;
		sseHandler = null;
		if (vertx != null) {
			vertx.close(context.asyncAssertSuccess()); // will shut down the server
		}
	}

	protected EventSource eventSource() {
		return EventSource.create(vertx, clientOptions());
	}

	protected HttpClient client() {
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
