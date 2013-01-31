package net.ion.crawler.auth;

import java.util.List;

import junit.framework.TestCase;
import net.ion.crawler.http.HTTPRequest;
import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.http.IHttpClient;
import net.ion.crawler.http.MyHttpClient;
import net.ion.framework.util.Debug;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;


public class FormLoginDemo extends TestCase{
	static final String LOGON_SITE = "nid.naver.com";
	static final int LOGON_PORT = 80;


	public void testLogin() throws Exception{

		IHttpClient client = MyHttpClient.create() ;
		client.setDefaultHost(LOGON_SITE, LOGON_PORT, "http");
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

		HTTPResponse get = client.createRequest("/nidlogin.login").get() ;
		List<Cookie> cookies = client.getCookieStore().getCookies() ;
		Debug.debug("Initial set of cookies:" + cookies);
		get.consume() ;

		HTTPRequest request = client.createRequest("/nidlogin.login") ;
		request.addParam("action", "https://nid.naver.com/nidlogin.login") ;
		request.addParam("url", "http://www.naver.com") ;
		request.addParam("svc", "me") ;
		request.addParam("id", "bleujin") ;
		request.addParam("pw", "redf3r") ;

		HTTPResponse authResponse = request.post() ;
		List<Cookie> logoncookies = client.getCookieStore().getCookies() ;
		Debug.debug("Logon cookies:", authResponse.getStatusCode(), logoncookies);
		
		Debug.line(authResponse.getEntityAsText(), authResponse.maybeCharSet()) ;
		authResponse.consume() ;
	}
}
