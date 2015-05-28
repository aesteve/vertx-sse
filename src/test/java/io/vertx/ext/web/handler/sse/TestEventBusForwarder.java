package io.vertx.ext.web.handler.sse;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class TestEventBusForwarder extends TestBase {

    private final static String EB_ADDR = "testAddress";
    private JsonObject msg;

    @Before
    public void registerEbForwarder(TestContext context) {
        sseHandler.connectHandler(connection -> {
            connection.forward(EB_ADDR);
        });
        msg = new JsonObject();
        msg.put("quote", "Happiness is a warm puppy");
        msg.put("author", "Charles M. Schulz");
    }

    @Test
    public void forwardData(TestContext context) {
        Async async = context.async();
        EventSource eventSource = eventSource();
        eventSource.onMessage(message -> {
            assertNotNull(message);
            assertEquals(msg, new JsonObject(message));
            async.complete();
        });
        eventSource.connect("/sse?token=" + TOKEN, handler -> {
            assertTrue(handler.succeeded());
            assertFalse(handler.failed());
            assertNull(handler.cause());
            vertx.eventBus().publish(EB_ADDR, msg);
        });
    }

    @Test
    public void forwardEvent(TestContext context) {
        Async async = context.async();
        EventSource eventSource = eventSource();
        String eventName = "someQuote";
        eventSource.onEvent(eventName, message -> {
            assertNotNull(message);
            assertEquals(msg, new JsonObject(message));
            async.complete();
        });
        eventSource.connect("/sse?token=" + TOKEN, handler -> {
            assertTrue(handler.succeeded());
            assertFalse(handler.failed());
            assertNull(handler.cause());
            DeliveryOptions options = new DeliveryOptions();
            options.addHeader("event", eventName);
            vertx.eventBus().publish(EB_ADDR, msg, options);
        });
    }

    @Test
    public void forwardId(TestContext context) {
        Async async = context.async();
        EventSource eventSource = eventSource();
        String id = "someQuote";
        eventSource.onMessage(message -> {
            assertNotNull(message);
            assertEquals(msg, new JsonObject(message));
            assertEquals(id, eventSource.lastId());
            async.complete();
        });
        eventSource.connect("/sse?token=" + TOKEN, handler -> {
            assertTrue(handler.succeeded());
            assertFalse(handler.failed());
            assertNull(handler.cause());
            DeliveryOptions options = new DeliveryOptions();
            options.addHeader("id", id);
            vertx.eventBus().publish(EB_ADDR, msg, options);
        });
    }
}
