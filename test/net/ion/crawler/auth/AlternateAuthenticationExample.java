package net.ion.crawler.auth;

import junit.framework.TestCase;
import net.ion.crawler.http.HTTPRequest;
import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.http.IHttpClient;
import net.ion.crawler.http.MyHttpClient;
import net.ion.framework.util.ListUtil;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;


public class AlternateAuthenticationExample extends TestCase{

	public void testGet() throws Exception {
		IHttpClient client = MyHttpClient.create() ;;
		client.setCredentials(new AuthScope("http://im.i-on.net/", 80, "I-ON MEMBERS"), new UsernamePasswordCredentials("bleujin", "bleujin7"));
		// Suppose the site supports several authetication schemes: NTLM and Basic
		// Basic authetication is considered inherently insecure. Hence, NTLM authentication
		// is used per default

		// This is to make HttpClient pick the Basic authentication scheme over NTLM & Digest
		client.setParameter(AuthPNames.TARGET_AUTH_PREF, ListUtil.toList(AuthPolicy.BASIC, AuthPolicy.NTLM, AuthPolicy.DIGEST));

		HTTPResponse response = null ;
		try {
			HTTPRequest request = client.createRequest("http://im.i-on.net/zeroboard/main.php") ;
			response = request.get() ;
			// print the status and response
			System.out.println(response.getStatusCode());
			System.out.println(response.getEntityAsText());
		} finally {
			response.consume() ;
			client.shutdown() ;
		}
	}
}
