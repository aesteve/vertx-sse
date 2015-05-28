package io.vertx.ext.web.handler.sse.exceptions;

import io.vertx.core.http.HttpClientResponse;

public class ConnectionRefusedException extends Exception {

    private static final long serialVersionUID = -6440236022622195797L;

    private HttpClientResponse response;

    public ConnectionRefusedException(HttpClientResponse response) {
        super(response.statusMessage());
        this.response = response;
    }

    public int statusCode() {
        return response.statusCode();
    }

    public String statusMessage() {
        return response.statusMessage();
    }
}
