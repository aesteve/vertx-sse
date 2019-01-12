package io.vertx.ext.web.handler.sse;

import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TestEventBusBridgeMapped extends TestBase {

	@Override
	public void addBridge(Router router) {
		EventBusSSEBridge bridge = EventBusSSEBridge.create().mapping(request -> request.getParam("id"));
		router.get("/bridge/mapped/:id").handler(bridge);
	}

	@Test
	void testBridge(VertxTestContext ctx) {
		String id = "someaddress";
		String url = "/bridge/mapped/" + id;
		String message = "sent over the event bus, on a mapped address";
		eventSource().connect(url, res -> {
			ctx.verify(() -> {
				assertFalse(res.failed());
			});
			vertx.eventBus().publish("anotheraddress", "should not be received");
			vertx.eventBus().publish(id, message);
		}).onMessage(msg -> {
			ctx.verify(() -> {
				assertEquals(message + "\n", msg);
				ctx.completeNow();
			});
		});
	}


	@Test
	void testTwoConnections() throws Exception {
		String id1 = "someaddress";
		String id2 = "anotheraddress";
		String url1 = "/bridge/mapped/" + id1;
		String url2 = "/bridge/mapped/" + id2;
		String message1 = "sent over the event bus, on address " + id1;
		String message2 = "sent over the event bus, on address " + id2;
		CountDownLatch latch = new CountDownLatch(2);
		eventSource().connect(url1, res -> {
			assertFalse(res.failed());
			eventSource().connect(url2, res2 -> {
				assertFalse(res2.failed());
				vertx.eventBus().publish(id2, message2);
			}).onMessage(msg -> {
				assertEquals(message2 + "\n", msg);
				latch.countDown();
			});
			vertx.eventBus().publish(id1, message1);
		}).onMessage(msg -> {
			assertEquals(message1 + "\n", msg);
			latch.countDown();
		});
		latch.await(1, TimeUnit.SECONDS);
	}

}
