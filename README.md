[![Travis](https://img.shields.io/travis/aesteve/vertx-sse.svg)](https://travis-ci.org/aesteve/vertx-sse)
[![Codecov](https://img.shields.io/codecov/c/github/aesteve/vertx-sse.svg)](https://codecov.io/gh/aesteve/vertx-sse)

# vertx-sse

Add Server-Sent-Events support to Vert.x by providing a vertx-web handler that deals with all the stuff required for server-sent-events.

Currently trying to integrate into vertx-web. If you have some time to help, just contact me, thanks.

## Server-side

Simply

```java
yourRouter.get("/sse").handler(SSEHandler.create());
```
But it won't do anything if you don't attach any handler to it.

```java
SSEHandler sse = SSEHandler.create();
sse.connectHandler(connection -> {
  // connection is an SSEConnection object you can push messages into
  // it wraps the HttpServerRequest used to establish the connection
});
sse.closeHandler(connection -> {
  // the same connection object as in the connect handler
});
```

### Example 1 : simply sends some data once the client is connected

```java
SSEHandler sse = SSEHandler.create();
sse.connectHandler(sseConnection -> {
  sseConnection.data("Welcome!");
});
yourRouter.get("/sse").handler(sse);
```

### Example 2 : send events periodically and keep track of connected users

```java
SSEHandler pingSSE = SSEHandler.create();
Map<SSEConnection, Long> timersPerConnection = new HashMap<SSEConnection, Long>();
pingSSE.connectHandler(sseConnnection -> {
  Long timerId = vertx.setPeriodic(1000, tId -> {
      sseConnection.data("ping ! "+ new Date().getTime());
  });
  timersPerConnection.put(sseConnection, timerId);
});
pingSSE.closeHandler(sseConnection -> {
    Long timerId = timersPerConnection.get(sseConnection);
    if (timerId != null) {
        vertx.cancelTimer(timerId);
    }
});
yourRouter.get("/sse").handler(pingSSE);
```

You can also reject a connection and specify an http status code / message.

```java
String magicToken = "theOneThatRulesThemAll";
SSEHandler sse = SSEHandler.create();
sse.connectHandler(sseConnection -> {
   HttpServerRequest request = sseConnection.request();
   String token = request.getParam("authenticationToken");
   if (token == null) {
     sseConnection.reject(401);
   } else if (!magicToken.equals(token)) {
     sseConnection.reject(403);
   }
});
yourRouter.get("/sse").handler(sse);
```

For further use cases, take a look at the tests.

## Client side

This project also provides a simple `EventSource` object that mimics the html5 EventSource API. So that you can create your own SSE clients programmatically.

### Example 1 : Establish connection

```java
HttpClientOptions options = new HttpClientOptions();
options.setDefaultHost("localhost");
options.setDefaultPort(9000);
EventSource eventSource = EventSource.create(vertx, options);
eventSource.connect(connectHandler -> {
   if (connectHandler.succeeded()) {
       // Yay ! you'll be able to receive events from the server
       eventSource.onMessage(msg -> {
         System.out.println("Message received : "+msg);
       });
   } else {
     ConnectionRefusedException cre = (ConnectionRefuseException)connectHandler.cause();
     System.out.println("Server dropped me because : " + cre.statusCode() + " and he told me : "+cre.statusMessage());
   }
});
```

You can register handlers on the `EventSource` object by calling : 
* onMessage
* onEvent

or ask for the last id sent by the server : `eventSource.lastId()` if you want to keep track of message ids (especially if you want to connect later).
