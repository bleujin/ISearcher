package net.ion.nsearcher.httpclient;

import junit.framework.TestCase;
import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.http.IHttpClient;
import net.ion.crawler.http.MyHttpClient;
import net.ion.framework.util.Debug;

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
