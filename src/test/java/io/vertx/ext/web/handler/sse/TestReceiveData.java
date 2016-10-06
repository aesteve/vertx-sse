package io.vertx.ext.web.handler.sse;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@RunWith(VertxUnitRunner.class)
public class TestReceiveData extends TestBase {

	@Test
	public void testSimpleDataHandler(TestContext context) {
		final Async async = context.async();
		final String message = "Happiness is a warm puppy";
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertTrue(handler.succeeded());
			context.assertFalse(handler.failed());
			context.assertNull(handler.cause());
			context.assertNotNull(connection);
			eventSource.onMessage(msg -> {
				context.assertEquals(message + "\n", msg);
				async.complete();
			});
			connection.data(message);
		});
	}

	@Test
	public void testMultipleDataHandler(TestContext context) {
		final Async async = context.async();
		final List<String> quotes = createData();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertTrue(handler.succeeded());
			context.assertFalse(handler.failed());
			context.assertNull(handler.cause());
			context.assertNotNull(connection);
			eventSource.onMessage(msg -> {
				final StringJoiner joiner = new StringJoiner("\n");
				quotes.forEach(joiner::add);
				context.assertEquals(joiner.toString() + "\n", msg);
				async.complete();
			});
			connection.data(quotes);
		});
	}

	@Test
	public void testEventHandler(TestContext context) {
		final Async async = context.async();
		final String eventName = "quotes";
		final List<String> quotes = createData();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertTrue(handler.succeeded());
			context.assertFalse(handler.failed());
			context.assertNull(handler.cause());
			context.assertNotNull(connection);
			eventSource.onEvent("wrong", msg -> {
				context.fail(); /* this handler should not be called, at all ! */
			});
			eventSource.onEvent(eventName, msg -> {
				final StringJoiner joiner = new StringJoiner("\n");
				quotes.forEach(joiner::add);
				context.assertEquals(joiner.toString() + "\n", msg);
				async.complete();
			});
			connection.event(eventName, quotes);
		});
	}

	@Test
	public void testId(TestContext context) {
		final Async async = context.async();
		final String id = "SomeIdentifier";
		final List<String> quotes = createData();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertTrue(handler.succeeded());
			context.assertFalse(handler.failed());
			context.assertNull(handler.cause());
			context.assertNotNull(connection);
			eventSource.onMessage(msg -> {
				final StringJoiner joiner = new StringJoiner("\n");
				quotes.forEach(joiner::add);
				context.assertEquals(joiner.toString() + "\n", msg);
				context.assertEquals("Client last id is OK", id, eventSource.lastId());
				eventSource.close();
				eventSource.connect("/sse?token=" + TOKEN, eventSource.lastId(), secondHandler -> {
					context.assertTrue(handler.succeeded());
					context.assertFalse(handler.failed());
					context.assertNull(handler.cause());
					context.assertNotNull(connection);
					context.assertEquals("Server last id is OK", id, connection.lastId());
					async.complete();
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
