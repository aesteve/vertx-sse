package io.vertx.ext.web.handler.sse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TestEventBusBridgeNoMapping extends TestBase {

	private final static String EB_ADDR = "testAddress";
	private JsonObject msg;

	@Override
	public void addBridge(Router router) {
		router.get("/bridge/*").handler(EventBusSSEBridge.create());
	}

	@Test
	void testBridge(VertxTestContext ctx) {
		final String address = "/bridge/address";
		final String message = "sent over the event bus";
		eventSource().connect(address, res -> {
			assertFalse(res.failed());
			vertx.eventBus().publish(address, message);
		}).onMessage(msg -> {
			assertEquals(message + "\n", msg);
			ctx.completeNow();
		});
	}


}
