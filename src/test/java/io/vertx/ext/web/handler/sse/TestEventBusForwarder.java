package io.vertx.ext.web.handler.sse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestEventBusForwarder extends TestBase {

	private final static String EB_ADDR = "testAddress";
	private JsonObject msg;

	@Before
	public void registerEbForwarder(TestContext context) {
		sseHandler.connectHandler(connection -> connection.forward(EB_ADDR));
		msg = new JsonObject();
		msg.put("quote", "Happiness is a warm puppy");
		msg.put("author", "Charles M. Schulz");
	}

	@Test
	public void forwardData(TestContext context) {
		final Async async = context.async();
		final EventSource eventSource = eventSource();
		eventSource.onMessage(message -> {
			context.assertNotNull(message);
			context.assertEquals(msg, new JsonObject(message));
			async.complete();
		});
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertTrue(handler.succeeded());
			context.assertFalse(handler.failed());
			context.assertNull(handler.cause());
			vertx.eventBus().publish(EB_ADDR, msg);
		});
	}

	@Test
	public void forwardEvent(TestContext context) {
		final Async async = context.async();
		final EventSource eventSource = eventSource();
		final String eventName = "someQuote";
		eventSource.onEvent(eventName, message -> {
			context.assertNotNull(message);
			context.assertEquals(msg, new JsonObject(message));
			async.complete();
		});
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertTrue(handler.succeeded());
			context.assertFalse(handler.failed());
			context.assertNull(handler.cause());
			final DeliveryOptions options = new DeliveryOptions();
			options.addHeader("event", eventName);
			vertx.eventBus().publish(EB_ADDR, msg, options);
		});
	}

	@Test
	public void forwardId(TestContext context) {
		final Async async = context.async();
		final EventSource eventSource = eventSource();
		final String id = "someQuote";
		eventSource.onMessage(message -> {
			context.assertNotNull(message);
			context.assertEquals(msg, new JsonObject(message));
			context.assertEquals(id, eventSource.lastId());
			async.complete();
		});
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertTrue(handler.succeeded());
			context.assertFalse(handler.failed());
			context.assertNull(handler.cause());
			final DeliveryOptions options = new DeliveryOptions();
			options.addHeader("id", id);
			vertx.eventBus().publish(EB_ADDR, msg, options);
		});
	}
}
