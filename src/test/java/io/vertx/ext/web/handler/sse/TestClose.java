package io.vertx.ext.web.handler.sse;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestClose extends TestBase {

	@Test
	public void closeHandlerOnServer(TestContext context) {
		final Async async = context.async();
		final EventSource eventSource = eventSource();
		eventSource.connect("/sse?token=" + TOKEN, handler -> {
			context.assertNotNull(connection);
			eventSource.close(); /* closed by client */
			try {
				Thread.sleep(100); /* we have to, since it happens on the server */
			} catch (InterruptedException ie) {}
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
