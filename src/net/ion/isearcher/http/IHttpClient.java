package net.ion.isearcher.http;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;

public interface IHttpClient extends HttpClient{

	void setCredentials(AuthScope authScope, Credentials credentials);

	Credentials getCredentials(AuthScope authScope);

	
	void setHttpRequestRetryHandler(HttpRequestRetryHandler handler) ;
	
	void setParameter(String key, Object val);

	<T> T getParameter(String key, Class<T> clz);

	void shutdown();

	HTTPRequest createRequest(String url);

	void setDefaultHost(String hostname, int port, String scheme);

	CookieStore getCookieStore();

}
