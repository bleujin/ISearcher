package net.ion.isearcher.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

public class MyHttpClient extends DefaultHttpClient implements IHttpClient {
	private static final String USER_AGENT = "ION-WebCrawler/0.1 (https://www.i-on.net)";

	private MyHttpClient(ClientConnectionManager cmanager) {
		super(cmanager);
		getParams().setParameter(AllClientPNames.USER_AGENT, USER_AGENT) ;
	}

	public static IHttpClient create() {
		return new MyHttpClient(new SingleClientConnManager());
	}

	public static IHttpClient create(boolean multiThreaded) {
		return multiThreaded ? new MyHttpClient(new ThreadSafeClientConnManager()) : create();
	}

	public void setDefaultHost(String hostname, int port, String scheme) {
		getParams().setParameter(ClientPNames.DEFAULT_HOST, new HttpHost(hostname, port, scheme));
	}

	public void setCredentials(AuthScope authScope, Credentials credentials) {
		getCredentialsProvider().setCredentials(authScope, credentials);
	}

	public Credentials getCredentials(AuthScope authScope) {

		return getCredentialsProvider().getCredentials(authScope);
	}

	public <T> T getParameter(String key, Class<T> clz) {
		Object val = getParams().getParameter(key);
		if (clz.isInstance(val))
			return clz.cast(val);
		return null;
	}

	public void setParameter(String key, Object val) {
		getParams().setParameter(key, val);
	}

	public void shutdown() {
		getConnectionManager().shutdown();
	}

	public HTTPRequest createRequest(String url) {
		return HTTPRequest.create(this, url);
	}

}
