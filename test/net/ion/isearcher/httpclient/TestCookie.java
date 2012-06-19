package net.ion.isearcher.httpclient;

import org.apache.http.client.CookieStore;

import net.ion.framework.util.Debug;
import net.ion.isearcher.http.HTTPResponse;
import net.ion.isearcher.http.IHttpClient;
import net.ion.isearcher.http.MyHttpClient;
import junit.framework.TestCase;

public class TestCookie extends TestCase{
	
	
	public void testCookie() throws Exception {
		IHttpClient client = MyHttpClient.create() ;
		client.setDefaultHost("www.google.com", 80, "http"); 
		HTTPResponse response = client.createRequest("/").get() ;
		
		CookieStore cs = client.getCookieStore() ;
		Debug.line(cs) ;
		response.consume();
	}
}
