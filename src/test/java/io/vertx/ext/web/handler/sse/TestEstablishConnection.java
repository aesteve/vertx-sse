package io.vertx.ext.web.handler.sse;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.handler.sse.exceptions.ConnectionRefusedException;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestEstablishConnection extends TestBase {

	@Test
	public void noToken(TestContext context) {
		final Async async = context.async();
		eventSource().connect("/sse", handler -> {
			context.assertFalse(handler.succeeded());
			context.assertTrue(handler.failed());
			context.assertNotNull(handler.cause());
			context.assertTrue(handler.cause() instanceof ConnectionRefusedException);
			final ConnectionRefusedException cre = (ConnectionRefusedException) handler.cause();
			context.assertEquals(401, cre.statusCode());
			async.complete();
		});
	}

	@Test
	public void invalidToken(TestContext context) {
		Async async = context.async();
		eventSource().connect("/sse?token=yourmum", handler -> {
			context.assertFalse(handler.succeeded());
			context.assertTrue(handler.failed());
			final Throwable cause = handler.cause();
			context.assertNotNull(cause);
			context.assertTrue(cause instanceof ConnectionRefusedException);
			final ConnectionRefusedException cre = (ConnectionRefusedException) cause;
			context.assertEquals(403, cre.statusCode());
			async.complete();
		});
	}

	@Test
	public void validConnection(TestContext context) {
		final Async async = context.async();
		eventSource().connect("/sse?token=" + TOKEN, handler -> {
			context.assertTrue(handler.succeeded());
			context.assertFalse(handler.failed());
			context.assertNull(handler.cause());
			async.complete();
		});
	}

}
