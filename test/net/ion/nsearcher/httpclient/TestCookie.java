package net.ion.nsearcher.httpclient;

import junit.framework.TestCase;
import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.http.IHttpClient;
import net.ion.crawler.http.MyHttpClient;
import net.ion.framework.util.Debug;

import org.apache.http.client.CookieStore;

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
