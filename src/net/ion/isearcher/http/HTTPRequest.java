package net.ion.isearcher.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.StringUtil;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.header.HeaderConstants;

public class HTTPRequest {

	private IHttpClient client ;
	private String url ;
	
	private Form params ;
	private Form headers = new Form() ;
	protected HTTPRequest(IHttpClient client, String url, Form params) {
		this.client = client ;
		this.url = url ;
		this.params = params;
	}

	public static HTTPRequest create(IHttpClient client, String url) {
		String[] getPath = StringUtil.split(url, '?');
		if (getPath.length == 1) {
			return new HTTPRequest(client, url, new Form());
		} else {
			Form form = new Form(getPath[1], CharacterSet.UTF_8);
			return new HTTPRequest(client, getPath[0], form);
		}
	}

	public HTTPResponse handle(Method method) throws IOException {
		HttpUriRequest request = createRequest(method);
		HTTPResponse result = HTTPResponse.create(request, client.execute(request));
		
		if (client.getParams().getParameter(ClientPNames.HANDLE_REDIRECTS) == null && Boolean.TRUE.equals(client.getParams().getParameter(ClientPNames.HANDLE_REDIRECTS)) ){
			return result ;
		}
		
		if (result.getStatusCode() == Status.REDIRECTION_FOUND.getCode()){
			String location = result.getFirstHeader(HeaderConstants.HEADER_LOCATION).getValue() ;
			if (location != null && (location.startsWith("http://") || location.startsWith("https://"))){ // absolute path, HTTP spec requires the location value be an absolute URI
				result.consume() ;
				result = client.createRequest(location).get() ;
			}
		}
		
		return result;
	}
	
	protected HttpUriRequest createRequest(Method method) throws IOException{
		HttpUriRequest request = createMethod(method) ;
		for (String headerName : headers.getNames()) {
			request.addHeader(headerName, headers.getValues(headerName)) ;
		}
		
		if (request instanceof HttpEntityEnclosingRequest){
			((HttpEntityEnclosingRequest)request).setEntity(getRequestEntity()) ;
		} 

		return request ;
	}

	public AbstractHttpEntity getRequestEntity() throws UnsupportedEncodingException {
		return new StringEntity(params.getQueryString(), MediaType.APPLICATION_WWW_FORM.toString(), "UTF-8") ;
	}
	
	private String queryString() throws IOException{
		return IOUtil.toString(getRequestEntity().getContent(), "UTF-8") ; 
	}
	
	private HttpUriRequest createMethod(Method method) throws IOException {
		
		if (Method.GET.equals(method)){
			return new HttpGet(url + "?" + queryString()) ;
		} else if (Method.POST.equals(method)){
			return new HttpPost(url) ;
		} else if (Method.DELETE.equals(method)){
			return new HttpDelete(url + "?" + queryString()) ;
		} else if (Method.PUT.equals(method)){
			return new HttpPut(url) ;
		} else if (Method.HEAD.equals(method)){
			return new HttpHead(url + "?" + queryString()) ;
		} else if (Method.OPTIONS.equals(method)){
			return new HttpOptions(url + "?" + queryString()) ;
		} else {
			throw new IllegalArgumentException(method + " is not http method") ;
		}
	}

	
	public HTTPRequest addParam(String key, String value){
		params.add(key, value) ;
		return this ;
	}
	
	public HTTPRequest addHeader(String key, String value){
		headers.add(key, value) ;
		return this ;
	}

	public HTTPResponse get() throws IOException {
		return handle(Method.GET) ;
	}

	public HTTPResponse post() throws IOException {
		return handle(Method.POST) ;
	}

	public HTTPResponse delete() throws IOException {
		return handle(Method.DELETE) ;
	}

	public HTTPResponse put() throws IOException {
		return handle(Method.PUT) ;
	}

	public void abort() {
		
	}
	
	protected IHttpClient getClient(){
		return client;
	}


}
