package io.vertx.ext.web.handler.sse;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestRequestResponseHeaders extends TestBase {

	@Test
	public void noHeaderTextEventStreamHttpRequest(TestContext context) {
		final Async async = context.async();
		client().get("/sse", response -> {
			context.assertEquals(406, response.statusCode());
			async.complete();
		}).putHeader("Accept", "foo").end();
	}

	@Test
	public void noHeaderHttpRequest(TestContext context) {
		final Async async = context.async();
		client().getNow("/sse", response -> {
			context.assertEquals("text/event-stream", response.getHeader("Content-Type"));
			context.assertEquals("no-cache", response.getHeader("Cache-Control"));
			context.assertEquals("keep-alive", response.getHeader("Connection"));
			async.complete();
		});
	}

	@Test
	public void correctResponseHeaders(TestContext context) {
		final Async async = context.async();
		client().get("/sse", response -> {
			context.assertEquals("text/event-stream", response.getHeader("Content-Type"));
			context.assertEquals("no-cache", response.getHeader("Cache-Control"));
			context.assertEquals("keep-alive", response.getHeader("Connection"));
			async.complete();
		}).putHeader("Accept", "text/event-stream").end();
	}
}
