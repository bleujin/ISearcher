package net.ion.isearcher.httpclient;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.isearcher.http.HTTPResponse;
import net.ion.isearcher.http.IHttpClient;
import net.ion.isearcher.http.MyHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class TestCharset extends TestCase{
	
	
	public void testCharSet() throws Exception {
		IHttpClient client = MyHttpClient.create() ;
		client.setDefaultHost("www.i-on.net", 80, "http"); 
		HTTPResponse response = client.createRequest("/").get() ;
		
		assertEquals(200, response.getStatusCode()) ;
		
		Debug.line(response.maybeCharSet(), response.getEntityAsText()) ;
		response.consume() ;
	}
	
}
