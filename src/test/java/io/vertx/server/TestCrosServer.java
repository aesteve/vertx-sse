/**
 * 
 */
package io.vertx.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;

/**
 * @author yangliu
 *
 */
public class TestCrosServer {
	private final static String HOST = "localhost";
	private final static Integer PORT = 9000;
	
	
	private HttpClient client;
	private Server server;
	
	@BeforeEach
	public void init() {
		
		//
		server=new Server();
		server.start();
		
		HttpClientOptions options = new HttpClientOptions();
		options.setDefaultHost(HOST);
		options.setDefaultPort(PORT);
		
		Vertx vertx = Vertx.vertx();
		client=vertx.createHttpClient(options);
		
	}
	
	@AfterEach
	public void destroy() {
		if(this.client!=null) {
			this.client.close();
		}
		if(this.server!=null) {
			this.server.stop();
		}
	}
	
	@Test
	public void test_getEventStream() {
		
		
		
		this.client.getNow("/sse", response->{
		
			assertEquals("text/event-stream", response.getHeader(HttpHeaders.CONTENT_TYPE));
			assertEquals("no-cache", response.getHeader(HttpHeaders.CACHE_CONTROL));
			assertEquals("keep-alive", response.getHeader(HttpHeaders.CONNECTION));
			
			
		});
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
