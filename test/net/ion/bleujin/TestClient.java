package net.ion.bleujin;

import java.util.Iterator;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import junit.framework.TestCase;

public class TestClient extends TestCase {

	public void testStatic() throws Exception {
		for (int i = 0; i < 100; i++) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet("http://www.daum.net");
			HttpResponse response = client.execute(get);
			String body = IOUtil.toString(response.getEntity().getContent());
			client.getConnectionManager().shutdown() ;
		}
	}
	
	public void testShutdown() throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://www.daum.net");
		HttpResponse response = client.execute(get);
		client.getConnectionManager().shutdown() ;
		String body = IOUtil.toString(response.getEntity().getContent());
		Debug.line(body) ;
	}
	
	public void testAradonClient() throws Exception {
		NewClient client = NewClient.create();
		for (int i = 0; i < 100; i++) {
			Response response = client.prepareGet("http://www.daum.net").execute().get();
			String body = response.getTextBody();
		}
		client.close() ;
	}
	
	
	
	
}
