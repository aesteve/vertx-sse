package io.vertx.ext.apex.sse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestReceiveData extends TestBase {

    @Test
    public void testSimpleDataHandler(TestContext context) {
        Async async = context.async();
        String message = "Happiness is a warm puppy";
        EventSource eventSource = eventSource();
        eventSource.connect("/sse?token=" + TOKEN, handler -> {
            assertTrue(handler.succeeded());
            assertFalse(handler.failed());
            assertNull(handler.cause());
            assertNotNull(connection);
            eventSource.onMessage(msg -> {
                assertEquals(message + "\n", msg);
                async.complete();
            });
            connection.data(message);
        });
    }

    @Test
    public void testMultipleDataHandler(TestContext context) {
        Async async = context.async();
        List<String> quotes = createData();
        EventSource eventSource = eventSource();
        eventSource.connect("/sse?token=" + TOKEN, handler -> {
            assertTrue(handler.succeeded());
            assertFalse(handler.failed());
            assertNull(handler.cause());
            assertNotNull(connection);
            eventSource.onMessage(msg -> {
                StringJoiner joiner = new StringJoiner("\n");
                quotes.forEach(phrase -> {
                    joiner.add(phrase);
                });
                assertEquals(joiner.toString() + "\n", msg);
                async.complete();
            });
            connection.data(quotes);
        });
    }

    @Test
    public void testEventHandler(TestContext context) {
        Async async = context.async();
        String eventName = "quotes";
        List<String> quotes = createData();
        EventSource eventSource = eventSource();
        eventSource.connect("/sse?token=" + TOKEN, handler -> {
            assertTrue(handler.succeeded());
            assertFalse(handler.failed());
            assertNull(handler.cause());
            assertNotNull(connection);
            eventSource.onEvent("wrong", msg -> {
                context.fail(); /* this handler should not be called, at all ! */
            });
            eventSource.onEvent(eventName, msg -> {
                StringJoiner joiner = new StringJoiner("\n");
                quotes.forEach(phrase -> {
                    joiner.add(phrase);
                });
                assertEquals(joiner.toString() + "\n", msg);
                async.complete();
            });
            connection.event(eventName, quotes);
        });
    }

    @Test
    public void testId(TestContext context) {
        Async async = context.async();
        String id = "SomeIdentifier";
        List<String> quotes = createData();
        EventSource eventSource = eventSource();
        eventSource.connect("/sse?token=" + TOKEN, handler -> {
            assertTrue(handler.succeeded());
            assertFalse(handler.failed());
            assertNull(handler.cause());
            assertNotNull(connection);
            eventSource.onMessage(msg -> {
                StringJoiner joiner = new StringJoiner("\n");
                quotes.forEach(phrase -> {
                    joiner.add(phrase);
                });
                assertEquals(joiner.toString() + "\n", msg);
                assertEquals("Client last id is OK", id, eventSource.lastId());
                eventSource.close();
                eventSource.connect("/sse?token=" + TOKEN, eventSource.lastId(), secondHandler -> {
                    assertTrue(handler.succeeded());
                    assertFalse(handler.failed());
                    assertNull(handler.cause());
                    assertNotNull(connection);
                    assertEquals("Server last id is OK", id, connection.lastId());
                    async.complete();
                });
            });
            connection.id(id, quotes);
        });
    }

    private List<String> createData() {
        List<String> data = new ArrayList<String>(3);
        data.add("Happiness is a warm puppy");
        data.add("Bleh!");
        data.add("That's the secret of life... replace one worry with another");
        return data;
    }

}
