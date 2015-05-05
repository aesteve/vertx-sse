package io.vertx.ext.apex.sse.impl;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.sse.SSEConnection;

public class SSEConnectionImpl implements SSEConnection {

    private RoutingContext context;
    private boolean rejected;

    public SSEConnectionImpl(RoutingContext context) {
        this.context = context;
    }

    @Override
    public SSEConnection reject(int code) {
    	return reject(code, null);
    }
    
    @Override
    public SSEConnection reject(int code, String reason) {
    	rejected = true;
    	HttpServerResponse response = context.response();
    	response.setStatusCode(code);
    	if (reason != null) {
    		response.setStatusMessage(reason);
    	}
    	response.end();
    	return this;
    }
    
    @Override
    public SSEConnection comment(String comment) {
    	context.response().write("comment: "+comment+"\n\n");
    	return this;
    }
    
    @Override
    public SSEConnection retry(Long delay, String... data) {
    	return withHeader("delay", delay.toString(), data);
    }
    
    @Override
    public SSEConnection data(String... data) {
        appendData(data);
        return this;
    }

    @Override
    public SSEConnection event(String eventName, String... data) {
        return withHeader("event", eventName, data);
    }
    
    @Override
    public SSEConnection id(String id, String... data) {
    	return withHeader("id", id, data);
    }

    @Override
    public SSEConnection close() {
    	context.response().close();
        return this;
    }

    @Override
    public HttpServerRequest request() {
    	return context.request();
    }
    
    @Override
    public boolean rejected() {
    	return rejected;
    }
    
    private SSEConnection withHeader(String headerName, String headerValue, String... data) {
    	HttpServerResponse response = context.response();
    	response.write(headerName + ": " + headerValue + "\n");
    	return this;
    }
    
    private void appendData(String... data) {
        for (int i = 0; i < data.length; i++) {
        	String separator = "\n";
        	if (i == data.length - 1) {
        		separator += "\n";
        	}
            context.response().write("data: " + data[i] + separator);
        }
    }
}
