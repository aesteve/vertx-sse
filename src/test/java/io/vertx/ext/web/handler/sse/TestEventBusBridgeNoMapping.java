package io.vertx.ext.web.handler.sse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestEventBusBridgeNoMapping extends TestBase {

	private final static String EB_ADDR = "testAddress";
	private JsonObject msg;

	@Override
	public void addBridge(Router router) {
		router.get("/bridge/*").handler(EventBusSSEBridge.create());
	}

	@Test
	public void testBridge(TestContext ctx) {
		Async async = ctx.async();
		final String address = "/bridge/address";
		final String message = "sent over the event bus";
		eventSource().connect(address, res -> {
			ctx.assertFalse(res.failed());
			vertx.eventBus().publish(address, message);
		}).onMessage(msg -> {
			ctx.assertEquals(message + "\n", msg);
			async.complete();
		});
	}


}
