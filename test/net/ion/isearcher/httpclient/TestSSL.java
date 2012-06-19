package net.ion.isearcher.httpclient;

import net.ion.framework.util.Debug;
import net.ion.isearcher.http.IHttpClient;
import net.ion.isearcher.http.MyHttpClient;
import junit.framework.TestCase;

public class TestSSL extends TestCase{

	
	public void testSSL() throws Exception {
		IHttpClient client = MyHttpClient.create() ;
		Debug.line(client.createRequest("https://nid.naver.com/nidlogin.login").get().getEntityAsText()) ;
	}
}
