package net.ion.isearcher.httpclient;

import org.apache.http.HttpEntity;

import net.ion.framework.util.Debug;
import net.ion.isearcher.http.HTTPResponse;
import net.ion.isearcher.http.IHttpClient;
import net.ion.isearcher.http.MyHttpClient;
import junit.framework.TestCase;

public class TestRequest extends TestCase{

	
	public void testHost() throws Exception {
		IHttpClient client = MyHttpClient.create() ;
		client.setDefaultHost("www.snowcat.co.kr", 80, "http"); 
		HTTPResponse response = client.createRequest("").get() ;
		
		assertEquals(200, response.getStatusCode()) ;
		
		Debug.line(response.getHeaders());
		response.consume() ;
	}
	
	
	
	
}
