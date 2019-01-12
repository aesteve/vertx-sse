package io.vertx.ext.web.handler.sse;

import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestClose extends TestBase {

	private void waitSafely() {
		try {
			Thread.sleep(100); // NOSONAR
		} catch (InterruptedException ie) {} // NOSONAR
	}

	@Test
	void closeHandlerOnServer(VertxTestContext context) {
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			assertTrue(handler.succeeded());
			assertNotNull(connection);
			eventSource.close(); /* closed by client */
			waitSafely();
			assertTrue(closeHandlerCalled, "Connection should have been closed on the server at this point");
			context.completeNow();
		});
	}

	@Test
	void closeHandlerOnClient(VertxTestContext context) {
		final EventSource eventSource = eventSource();
		eventSource.onClose(handler -> context.completeNow());
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			assertNotNull(connection);
			connection.close();
		});
	}

}
