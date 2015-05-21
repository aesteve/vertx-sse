package io.vertx.ext.web.sse;

import static org.junit.Assert.*;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.sse.exceptions.ConnectionRefusedException;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestEstablishConnection extends TestBase {

    @Test
    public void noToken(TestContext context) {
        Async async = context.async();
        eventSource().connect("/sse", handler -> {
            assertFalse(handler.succeeded());
            assertTrue(handler.failed());
            assertNotNull(handler.cause());
            assertTrue(handler.cause() instanceof ConnectionRefusedException);
            ConnectionRefusedException cre = (ConnectionRefusedException) handler.cause();
            assertEquals(401, cre.statusCode());
            async.complete();
        });
    }

    @Test
    public void invalidToken(TestContext context) {
        Async async = context.async();
        eventSource().connect("/sse?token=yourmum", handler -> {
            assertFalse(handler.succeeded());
            assertTrue(handler.failed());
            assertNotNull(handler.cause());
            assertTrue(handler.cause() instanceof ConnectionRefusedException);
            ConnectionRefusedException cre = (ConnectionRefusedException) handler.cause();
            assertEquals(403, cre.statusCode());
            async.complete();
        });
    }

    @Test
    public void validConnection(TestContext context) {
        Async async = context.async();
        eventSource().connect("/sse?token=" + TOKEN, handler -> {
            assertTrue(handler.succeeded());
            assertFalse(handler.failed());
            assertNull(handler.cause());
            async.complete();
        });
    }

}
