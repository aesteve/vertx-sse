package io.vertx.ext.web.handler.sse;

import static org.junit.Assert.assertEquals;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestRequestResponseHeaders extends TestBase {

	@Test
	public void noHeaderHttpRequest(TestContext context) {
		Async async = context.async();
		client().getNow("/sse", response -> {
			assertEquals(406, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void correctResponseHeaders(TestContext context) {
		Async async = context.async();
		client().get("/sse", response -> {
			assertEquals("text/event-stream", response.getHeader("Content-Type"));
			assertEquals("no-cache", response.getHeader("Cache-Control"));
			assertEquals("keep-alive", response.getHeader("Connection"));
			async.complete();
		}).putHeader("Accept", "text/event-stream").end();
	}
}
