package net.ion.nsearcher.httpclient;

import junit.framework.TestCase;
import net.ion.crawler.http.IHttpClient;
import net.ion.crawler.http.MyHttpClient;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;

public class TestSSL extends TestCase{

	
	public void testSSL() throws Exception {
		IHttpClient client = MyHttpClient.create() ;
		Debug.line(client.createRequest("https://nid.naver.com/nidlogin.login").get().getEntityAsText()) ;
	}
	
	public void testNewClient() throws Exception {
		NewClient nc = NewClient.create();
		final Response response = nc.prepareGet("https://nid.naver.com/nidlogin.login").execute().get();
		Debug.line(response.getHeader("charset"), response.getHeaders()) ;
		
		Debug.line(response.getTextBody()) ;
	}
}
