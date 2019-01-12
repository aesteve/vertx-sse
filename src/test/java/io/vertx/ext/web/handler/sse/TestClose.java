package io.vertx.ext.web.handler.sse;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestClose extends TestBase {

	private void waitSafely() {
		try {
			Thread.sleep(2000); // NOSONAR
		} catch (InterruptedException ie) {} // NOSONAR
	}

	@Test
	public void closeHandlerOnServer(TestContext context) {
		final Async async = context.async();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertNotNull(connection);
			eventSource.close(); /* closed by client */
			waitSafely();
			context.assertNull(connection);
			async.complete();
		});
	}

	@Test
	public void closeHandlerOnClient(TestContext context) {
		final Async async = context.async();
		final EventSource eventSource = eventSource();
		eventSource.onClose(handler -> async.complete());
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertNotNull(connection);
			connection.close();
		});
	}

}
