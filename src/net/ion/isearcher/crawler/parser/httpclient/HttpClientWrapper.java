package net.ion.isearcher.crawler.parser.httpclient;

import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.http.HTTPRequest;
import net.ion.isearcher.http.IHttpClient;
import net.ion.isearcher.http.MyHttpClient;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;

/**
 * Contains abstract methods for the DownloadHelper and SimpleHttpClientParser.
 *
 * @author bleujin
 */
public abstract class HttpClientWrapper {

    private IHttpClient client;

    protected HttpClientWrapper(boolean multiThreaded) {
    	this.client = MyHttpClient.create(multiThreaded) ;
    }

    protected IHttpClient getClient() {
    	return this.client ;
    }
    
    
    public void setProxy(String proxyHost, int proxyPort) {
        client.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxyHost, proxyPort));
    }
    
    public void setParameter(String name, Object value){
    	client.setParameter(name, value) ;
    }

    public String getProxyHost() {
        HttpHost phost = client.getParameter(ConnRoutePNames.DEFAULT_PROXY, HttpHost.class);
		return phost != null ? phost.getHostName() : null;
    }

    public int getProxyPort() {
        HttpHost phost = client.getParameter(ConnRoutePNames.DEFAULT_PROXY, HttpHost.class);
		return phost != null ? phost.getPort() : -1;
    }

    public void setCredentials(AuthScope authscope, Credentials credentials) {
        client.setCredentials(authscope, credentials);
    }

    public Credentials getCredentials(AuthScope authscope) {
        return client.getCredentials(authscope);
    }
    
    public ClientConnectionManager getHttpConnectionManager() {
        return client.getConnectionManager();
    }

	public HTTPRequest createRequest(Link link) {
		return client.createRequest(link.getURI());
	}

}
