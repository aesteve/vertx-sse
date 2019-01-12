package io.vertx.ext.web.handler.sse;

import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRequestResponseHeaders extends TestBase {

	@Test
	void noHeaderTextEventStreamHttpRequest(VertxTestContext context) {
		client().get("/sse", response -> {
			context.verify(() -> {
				assertEquals(406, response.statusCode());
				context.completeNow();
			});
		}).putHeader("Accept", "foo").end();
	}

	@Test
	void noHeaderHttpRequest(VertxTestContext context) {
		client().getNow("/sse", response -> {
			assertSSEHeaders(context, response);
			context.completeNow();
		});
	}

	@Test
	void correctResponseHeaders(VertxTestContext context) {
		client().get("/sse", response -> {
			assertSSEHeaders(context, response);
			context.completeNow();
		}).putHeader("Accept", "text/event-stream").end();
	}

	private void assertSSEHeaders(VertxTestContext ctx, HttpClientResponse response) {
		ctx.verify(() -> {
			assertEquals("text/event-stream", response.getHeader(HttpHeaders.CONTENT_TYPE));
			assertEquals("no-cache", response.getHeader(HttpHeaders.CACHE_CONTROL));
			assertEquals("keep-alive", response.getHeader(HttpHeaders.CONNECTION));
		});
	}
}
