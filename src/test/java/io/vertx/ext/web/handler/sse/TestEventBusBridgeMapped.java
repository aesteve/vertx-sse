package io.vertx.ext.web.handler.sse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TestEventBusBridgeMapped extends TestBase {

	private final static String EB_ADDR = "testAddress";
	private JsonObject msg;

	@Override
	public void addBridge(Router router) {
		EventBusSSEBridge bridge = EventBusSSEBridge.create().mapping(request -> request.getParam("id"));
		router.get("/bridge/mapped/:id").handler(bridge);
	}

	@Test
	public void testBridge(TestContext ctx) {
		Async async = ctx.async();
		final String id = "someaddress";
		final String url = "/bridge/mapped/" + id;
		final String message = "sent over the event bus, on a mapped address";
		eventSource().connect(url, res -> {
			ctx.assertFalse(res.failed());
			vertx.eventBus().publish("anotheraddress", "should not be received");
			vertx.eventBus().publish(id, message);
		}).onMessage(msg -> {
			ctx.assertEquals(message + "\n", msg);
			async.complete();
		});
	}

}
