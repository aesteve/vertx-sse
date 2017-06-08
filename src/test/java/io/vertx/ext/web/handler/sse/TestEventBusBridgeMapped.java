package io.vertx.ext.web.handler.sse;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
		String id = "someaddress";
		String url = "/bridge/mapped/" + id;
		String message = "sent over the event bus, on a mapped address";
		eventSource().connect(url, res -> {
			ctx.assertFalse(res.failed());
			vertx.eventBus().publish("anotheraddress", "should not be received");
			vertx.eventBus().publish(id, message);
		}).onMessage(msg -> {
			ctx.assertEquals(message + "\n", msg);
			async.complete();
		});
	}


	@Test
	public void testTwoConnections(TestContext ctx) throws Exception {
		String id1 = "someaddress";
		String id2 = "anotheraddress";
		String url1 = "/bridge/mapped/" + id1;
		String url2 = "/bridge/mapped/" + id2;
		String message1 = "sent over the event bus, on address " + id1;
		String message2 = "sent over the event bus, on address " + id2;
		CountDownLatch latch = new CountDownLatch(2);
		eventSource().connect(url1, res -> {
			ctx.assertFalse(res.failed());
			eventSource().connect(url2, res2 -> {
				ctx.assertFalse(res2.failed());
				vertx.eventBus().publish(id2, message2);
			}).onMessage(msg -> {
				ctx.assertEquals(message2 + "\n", msg);
				latch.countDown();
			});
			vertx.eventBus().publish(id1, message1);
		}).onMessage(msg -> {
			ctx.assertEquals(message1 + "\n", msg);
			latch.countDown();
		});
		latch.await(1, TimeUnit.SECONDS);
	}

}
