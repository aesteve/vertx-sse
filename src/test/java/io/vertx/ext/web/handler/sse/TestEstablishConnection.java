package io.vertx.ext.web.handler.sse;

import io.vertx.ext.web.handler.sse.exceptions.ConnectionRefusedException;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestEstablishConnection extends TestBase {

	@Test
	void noToken(VertxTestContext context) {
		eventSource().connect("/sse", handler -> {
			context.verify(() -> {
				assertFalse(handler.succeeded());
				assertTrue(handler.failed());
				assertNotNull(handler.cause());
				assertTrue(handler.cause() instanceof ConnectionRefusedException);
				final ConnectionRefusedException cre = (ConnectionRefusedException) handler.cause();
				assertEquals(401, cre.statusCode());
				context.completeNow();
			});
		});
	}

	@Test
	void invalidToken(VertxTestContext context) {
		eventSource().connect("/sse?token=yourmum", handler -> {
			context.verify(() -> {
				assertFalse(handler.succeeded());
				assertTrue(handler.failed());
				final Throwable cause = handler.cause();
				assertNotNull(cause);
				assertTrue(cause instanceof ConnectionRefusedException);
				final ConnectionRefusedException cre = (ConnectionRefusedException) cause;
				assertEquals(403, cre.statusCode());
				context.completeNow();
			});
		});
	}

	@Test
	void validConnection(VertxTestContext context) {
		eventSource().connect("/sse?token=" + TOKEN, handler -> {
			context.verify(() -> {
				assertTrue(handler.succeeded());
				assertFalse(handler.failed());
				assertNull(handler.cause());
				context.completeNow();
			});
		});
	}

}
