package net.ion.isearcher.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

import net.ion.framework.util.IOUtil;
import net.ion.isearcher.crawler.parser.httpclient.DetectEncodingInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class HTTPResponse {

	private HttpUriRequest request;
	private HttpResponse iresponse;
	private DetectEncodingInputStream detectStream = null;
	private String maybeCharset = null;

	private HTTPResponse(HttpUriRequest request, HttpResponse iresponse) {
		this.request = request;
		this.iresponse = iresponse;
	}

	public static HTTPResponse create(HttpUriRequest request, HttpResponse iresponse) {
		return new HTTPResponse(request, iresponse);
	}

	public int getStatusCode() {
		return iresponse.getStatusLine().getStatusCode();
	}

	public String getEntityAsText() throws IOException {
		String maybeCharSet = maybeCharSet();
		Reader reader = new InputStreamReader(getStream(), maybeCharSet);
		String result = IOUtil.toString(reader);
		IOUtil.closeQuietly(reader);
		return result;
	}

	public Header getFirstHeader(String key) {
		return iresponse.getFirstHeader(key);
	}

	public Header[] getHeaders() {
		return iresponse.getAllHeaders();
	}

	public void consume() {
		try {
			EntityUtils.consume(iresponse.getEntity());
		} catch (IOException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}
	}

	public String getStatusLine() {
		return iresponse.getStatusLine().toString();
	}

	public String getPath() {
		return request.getURI().getPath();
	}

	public String maybeCharSet() {
		if (maybeCharset != null)
			return maybeCharset;

		String charset = EntityUtils.getContentCharSet(iresponse.getEntity());
		try {
			if (charset == null)
				return detectCharset();

			Charset c = Charset.forName(charset);
			return c.displayName();
		} catch (UnsupportedCharsetException ex) {
			return detectCharset();
		}
	}

	private String detectCharset() {
		try {
			InputStream bodyStream = getEntity().getContent();
			if (bodyStream == null || bodyStream.available() < 0) {
				return Charset.defaultCharset().toString();
			}
			detectStream = new DetectEncodingInputStream(bodyStream, 2048, Locale.getDefault());
			this.maybeCharset = detectStream.getEncoding().displayName();
			return maybeCharset;
		} catch (IOException ex) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex) ;
		}
	}

	public InputStream getStream() throws IOException {
		if (detectStream == null)
			return getEntity().getContent();
		return detectStream;
	}

	private HttpEntity getEntity() {
		return iresponse.getEntity();
	}

}
