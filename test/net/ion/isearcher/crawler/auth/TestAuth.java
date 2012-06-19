package net.ion.isearcher.crawler.auth;

import java.net.URLEncoder;
import java.util.List;

import javax.swing.plaf.ListUI;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.crawler.Crawler;
import net.ion.isearcher.crawler.filter.FileExtensionFilter;
import net.ion.isearcher.crawler.model.MaxDepthModel;
import net.ion.isearcher.crawler.parser.httpclient.SimpleHttpClientParser;
import net.ion.isearcher.crawler.parser.link.LinkExtractorBleujin;
import net.ion.isearcher.http.HTTPRequest;
import net.ion.isearcher.http.HTTPResponse;
import net.ion.isearcher.http.IHttpClient;
import net.ion.isearcher.http.MyHttpClient;
import net.ion.isearcher.indexer.report.DefaultReporter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
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
