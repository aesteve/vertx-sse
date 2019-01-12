package io.vertx.ext.web.handler.sse;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestEventBusForwarder extends TestBase {

	private final static String EB_ADDR = "testAddress";
	private JsonObject msg;

	@BeforeEach
	void registerEbForwarder() {
		sseHandler.connectHandler(connection -> connection.forward(EB_ADDR));
		msg = new JsonObject();
		msg.put("quote", "Happiness is a warm puppy");
		msg.put("author", "Charles M. Schulz");
	}

	@Test
	void forwardData(VertxTestContext context) {
		final EventSource eventSource = eventSource();
		eventSource.onMessage(message -> {
			context.verify(() -> {
				assertNotNull(message);
				assertEquals(msg, new JsonObject(message));
				context.completeNow();
			});
		});
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.verify(() -> {
				assertTrue(handler.succeeded());
				assertFalse(handler.failed());
				assertNull(handler.cause());
			});
			vertx.eventBus().publish(EB_ADDR, msg);
		});
	}

	@Test
	void forwardEvent(VertxTestContext context) {
		final EventSource eventSource = eventSource();
		final String eventName = "someQuote";
		eventSource.onEvent(eventName, message -> {
			context.verify(() -> {
				assertNotNull(message);
				assertEquals(msg, new JsonObject(message));
			});
			context.completeNow();
		});
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.verify(() -> {
				assertTrue(handler.succeeded());
				assertFalse(handler.failed());
				assertNull(handler.cause());
			});
			final DeliveryOptions options = new DeliveryOptions();
			options.addHeader("event", eventName);
			vertx.eventBus().publish(EB_ADDR, msg, options);
		});
	}

	@Test
	void forwardId(VertxTestContext context) {
		final EventSource eventSource = eventSource();
		final String id = "someQuote";
		eventSource.onMessage(message -> {
			context.verify(() -> {
				assertNotNull(message);
				assertEquals(msg, new JsonObject(message));
				assertEquals(id, eventSource.lastId());
				context.completeNow();
			});
		});
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.verify(() -> {
				assertTrue(handler.succeeded());
				assertFalse(handler.failed());
				assertNull(handler.cause());
			});
			final DeliveryOptions options = new DeliveryOptions();
			options.addHeader("id", id);
			vertx.eventBus().publish(EB_ADDR, msg, options);
		});
	}
}
