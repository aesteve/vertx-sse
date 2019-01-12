package io.vertx.ext.web.handler.sse;

import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReceiveData extends TestBase {

	@Test
	void testSimpleDataHandler(VertxTestContext context) {
		final String message = "Happiness is a warm puppy";
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			assertTrue(handler.succeeded());
			assertFalse(handler.failed());
			assertNull(handler.cause());
			assertNotNull(connection);
			eventSource.onMessage(msg -> {
				assertEquals(message + "\n", msg);
				context.completeNow();
			});
			connection.data(message);
		});
	}

	@Test
	void testMultipleDataHandler(VertxTestContext context) {
		final List<String> quotes = createData();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			assertTrue(handler.succeeded());
			assertFalse(handler.failed());
			assertNull(handler.cause());
			assertNotNull(connection);
			eventSource.onMessage(msg -> {
				final StringJoiner joiner = new StringJoiner("\n");
				quotes.forEach(joiner::add);
				assertEquals(joiner.toString() + "\n", msg);
				context.completeNow();
			});
			connection.data(quotes);
		});
	}

	@Test
	void testConsecutiveDataHandler(VertxTestContext context) {
		final List<String> quotes = createData();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			assertTrue(handler.succeeded());
			assertFalse(handler.failed());
			assertNull(handler.cause());
			assertNotNull(connection);
			final List<String> received = new ArrayList<>();

			eventSource.onMessage(msg -> {
				received.add(msg.substring(0, msg.length() - 1)); /* remove trailing linefeed */
				if (received.size() == quotes.size()) {
					assertEquals(quotes, received, "Received quotes don't match");
					context.completeNow();
				}
			});
			quotes.forEach(connection::data);
		});
	}

	@Test
	void testEventHandler(VertxTestContext context) {
		final String eventName = "quotes";
		final List<String> quotes = createData();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			assertTrue(handler.succeeded());
			assertFalse(handler.failed());
			assertNull(handler.cause());
			assertNotNull(connection);
			eventSource.onEvent("wrong", msg -> {
				throw new RuntimeException("this handler should not be called, at all !");
			});
			eventSource.onEvent(eventName, msg -> {
				final StringJoiner joiner = new StringJoiner("\n");
				quotes.forEach(joiner::add);
				assertEquals(joiner.toString() + "\n", msg);
				context.completeNow();
			});
			connection.event(eventName, quotes);
		});
	}

	@Test
	void testId(VertxTestContext context) {
		final String id = "SomeIdentifier";
		final List<String> quotes = createData();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			assertTrue(handler.succeeded());
			assertFalse(handler.failed());
			assertNull(handler.cause());
			assertNotNull(connection);
			eventSource.onMessage(msg -> {
				final StringJoiner joiner = new StringJoiner("\n");
				quotes.forEach(joiner::add);
				assertEquals(joiner.toString() + "\n", msg);
				assertEquals(id, eventSource.lastId());
				eventSource.close();
				eventSource.connect("/sse?token=" + TOKEN, eventSource.lastId(), secondHandler -> {
					assertTrue(handler.succeeded());
					assertFalse(handler.failed());
					assertNull(handler.cause());
					assertNotNull(connection);
					assertEquals(id, connection.lastId());
					context.completeNow();
				});
			});
			connection.id(id, quotes);
		});
	}

	private List<String> createData() {
		final List<String> data = new ArrayList<>(3);
		data.add("Happiness is a warm puppy");
		data.add("Bleh!");
		data.add("That's the secret of life... replace one worry with another");
		return data;
	}

}
