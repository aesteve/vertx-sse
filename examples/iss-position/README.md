## Live ISS Position

A very simple project using [The Open Notify API](http://open-notify.org/Open-Notify-API/) and [Leaflet](http://leafletjs.com) to show where the International Space Station is, right now.


The Open Notify is a pure (and free, with no auth needed, thanks mates ! that's awesome for a demo) JSON API sending the coordinates of the ISS when you make the call.

The API is located [here](http://api.open-notify.org/iss-now.json).

### Launching

You should first install `vertx-sse` to your local maven repository. Navigate to the root of this project and type :

```
./gradlew install
```

Then, from this directory, simply type :

```
./gradlew run
```

Then point your browser at : [http://localhost:9001](http://localhost:9001)


### Explanations

You can see there's just a single Verticle (`ISSVerticle`) which : 
- starts an http server on port 9001
- uses a vertx-web router to define static files
- reroutes `/` to `/static/index.html`

But that's not really what we're interested about in this example.

The most interesting parts are : 

```java
sse.connectHandler(connection -> {
    connection.forward(EB_ADDRESS);
});
router.get("/iss/position").handler(sse);
```

Which means that every EventSource connection made to `/iss/position` will receive messages sent over the event-bus on the `EB_ADDRESS` address.

Then, a periodic task fetches the ISS Position using the Open Notify API every 10 seconds.

```java
client.getNow("/iss-now.json", resp -> {
    if (resp.statusCode() != 200) {
        LOG.error("Could not fetch ISS position {}", resp.statusCode());
        return;
    }
    resp.bodyHandler(buff -> {
        JsonObject json = buff.toJsonObject();
        if (!"success".equals(json.getString("message"))) {
            LOG.error("Could not fetch ISS position {}", json.toString());
            return;
        }
        JsonObject position = json.getJsonObject("iss_position");
        vertx.eventBus().publish(EB_ADDRESS, position);
    });
});
```

When the position is received, it's published over the event-bus on `EB_ADDRESS`.

Which means, thanks to the `forward` usage, that every EventSource connected will receive the position.

Let's have a look at client side : `webroot/iss.js`

```javascript
var es = new EventSource('/iss/position');
es.onmessage = function(msg) {
    moveISS(JSON.parse(msg.data));
};
```

Simply creates a new EventSource, connected to `/iss/position` and calls the `moveISS` methods whenever it receives a new message.
The `moveISS` is nothing but displaying a marker on a leaflet map.
