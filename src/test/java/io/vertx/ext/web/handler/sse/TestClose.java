package io.vertx.ext.web.handler.sse;

import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
class TestClose extends TestBase {

	private void waitSafely() {
		try {
			Thread.sleep(100); // NOSONAR
		} catch (InterruptedException ie) {} // NOSONAR
	}

	@Test
	@Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
	void closeHandlerOnServer(VertxTestContext context) {
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.verify(() -> {
				assertTrue(handler.succeeded());
				assertNotNull(connection);
                sseHandler.closeHandler(sse -> {
                    context.completeNow();
                });
                waitSafely();
				eventSource.close(); /* closed by client */
			});
		});
	}

	@Test
	void closeHandlerOnClient(VertxTestContext context) {
		final EventSource eventSource = eventSource();
		eventSource.onClose(handler -> context.completeNow());
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.verify(() -> {
				assertNotNull(connection);
			});
			connection.close();
		});
	}

}
