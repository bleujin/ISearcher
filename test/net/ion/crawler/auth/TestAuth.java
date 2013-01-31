package net.ion.crawler.auth;

import net.ion.crawler.Crawler;
import net.ion.crawler.filter.FileExtensionFilter;
import net.ion.crawler.http.HTTPRequest;
import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.http.IHttpClient;
import net.ion.crawler.http.MyHttpClient;
import net.ion.crawler.model.MaxDepthModel;
import net.ion.crawler.parser.httpclient.SimpleHttpClientParser;
import net.ion.crawler.parser.link.LinkExtractorBleujin;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.index.report.DefaultReporter;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;
import org.restlet.engine.header.HeaderConstants;

public class TestAuth extends ISTestCase{

	
	public void testBasicAuth() throws Exception {
		String server ="http://im.i-on.net" ;
		String start = "/zeroboard/main.php" ;
		// String start = "/zeroboard/?s_url=/zeroboard/main.php" ;
		
		AuthScope authscope = new AuthScope("im.i-on.net", 80, "I-ON MEMBERS"); // alert hostName not include "http://"..
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bleujin", "redfpark");
		BasicAuthRealm auth = new BasicAuthRealm(authscope, credentials) ;

		
		FileExtensionFilter filter = new FileExtensionFilter(new String[] { ".html", ".htm", "/", ".css" , ".php" });

		Crawler crawler = new Crawler() ;
		
		SimpleHttpClientParser parser = new SimpleHttpClientParser();
		parser.setLinkExtractor(new LinkExtractorBleujin()) ;
		// parser.setCredentials(authscope, credentials) ;
		
		crawler.setAuth(auth) ;
		crawler.setLinkFilter(filter) ;

		crawler.setParser(parser) ;
		crawler.setModel(new MaxDepthModel(3)) ;
        // crawler.addParserListener(new ConfirmReportor()) ;
        crawler.setStartPage(server, start) ;
        crawler.collect() ;
	}

	public void testCookieAuth() throws Exception {
		String server ="http://www.naver.com" ;
		String start = "/" ;

		NameValuePair[] formNameValuePairs = new NameValuePair[]{
			new BasicNameValuePair("action", "http://nid.naver.com/nidlogin.login"),
			new BasicNameValuePair("url", ""),
			new BasicNameValuePair("id", "bleujin"),
			new BasicNameValuePair("password", "redfpark")
		} ;
		
		LoginSetting sample = new LoginSetting("http://nid.naver.com/nidlogin.login", formNameValuePairs) ;
		CookieAuth auth = new CookieAuth(sample) ;

		Crawler crawler = new Crawler() ;
		crawler.setAuth(auth) ;
		crawler.setModel(new MaxDepthModel(0)) ;
        crawler.addParserListener(new DefaultReporter(false)) ;
        crawler.setStartPage(server, start) ;
        crawler.collect() ;
	}

	public void testCookieAuth2() throws Exception {

		NameValuePair[] formNameValuePairs = new NameValuePair[]{
			new BasicNameValuePair("action", "http://dev-ibr.i-on.net/ibr/login.do"),
			new BasicNameValuePair("forwardName", "page"),
			new BasicNameValuePair("userId", "bleujin"),
			new BasicNameValuePair("userPwd", "1111")
		} ;
		
		LoginSetting sample = new LoginSetting("http://dev-ibr.i-on.net/ibr/login.do", formNameValuePairs) ;
		
		CookieAuth auth = new CookieAuth(sample) ;

		Crawler crawler = new Crawler() ;
		crawler.setAuth(auth) ;
		crawler.setModel(new MaxDepthModel(4)) ;
        crawler.setStartPage("http://dev-ibr.i-on.net", "/ibr/main.do") ;
        crawler.collect() ;
	}
	
	public void testLogin() throws Exception{
		IHttpClient client = MyHttpClient.create() ;

		HTTPRequest request = client.createRequest("http://dev-ibr.i-on.net/ibr/login.do") ;
		request.addParam("forwardName", "page") ;
		request.addParam("userId", "bleujin") ;
		request.addParam("userPwd", "1111") ;

		HTTPResponse response = request.post() ;
		assertEquals(200, response.getStatusCode()) ;
		assertEquals("UTF-8", response.maybeCharSet()) ;

		Debug.line(response.getEntityAsText()) ;
		response.consume() ;
	}
	
	
	public void testGet() throws Exception {
		IHttpClient client = MyHttpClient.create() ;
		HTTPResponse response = client.createRequest("http://dev-ibr.i-on.net/ibr/login.do?userId=bleujin&userPwd=1111").get();
		String text = response.getEntityAsText() ;
		Debug.line(text, response.getStatusCode(), response.getFirstHeader(HeaderConstants.HEADER_LOCATION)) ;
	}

	
	
	
	

}
