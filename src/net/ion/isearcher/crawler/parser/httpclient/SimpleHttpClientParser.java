package net.ion.isearcher.crawler.parser.httpclient;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import net.ion.isearcher.crawler.auth.IAuth;
import net.ion.isearcher.crawler.handler.BinaryHandler;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.parser.IParser;
import net.ion.isearcher.crawler.parser.PageData;
import net.ion.isearcher.crawler.parser.PageData.DataType;
import net.ion.isearcher.crawler.parser.link.LinkExtractorBleujin;
import net.ion.isearcher.crawler.util.HttpClientUtil;
import net.ion.isearcher.crawler.util.ILinkExtractor;
import net.ion.isearcher.http.HTTPRequest;
import net.ion.isearcher.http.HTTPResponse;
import net.ion.isearcher.http.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.restlet.data.Status;
import org.restlet.engine.header.HeaderConstants;

public class SimpleHttpClientParser extends HttpClientWrapper implements IParser {

	private static final transient Log LOG = LogFactory.getLog(SimpleHttpClientParser.class);

	public static final String USER_AGENT = "ION-WebCrawler/0.1 (https://www.i-on.net)";
	/** user agent HTTP header of the crawler. */

	private ILinkExtractor linkExtractor = new LinkExtractorBleujin();
	/** set the default link extractor of LinksUtil. */
	private BinaryHandler binaryHandler = BinaryHandler.ABORT_HANDLER;
	private HttpExceptionHandler exceptionHandler = HttpExceptionHandler.DEFAULT;

	public SimpleHttpClientParser() {
		this(false);
	}

	public SimpleHttpClientParser(boolean multiThreaded) {
		super(multiThreaded);
		getClient().setHttpRequestRetryHandler(new MyRequestRetryHandler());

	}
	
	private static class MyRequestRetryHandler implements HttpRequestRetryHandler {
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			if (executionCount >= 3) {
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				return true; // Retry if the server dropped connection on us
			}
			if (exception instanceof SSLHandshakeException) {
				return false; // Do not retry on SSL handshake exception
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
			if (idempotent) {
				return true;
			}
			return false;
		}
	}
	

	public void authProgress(IAuth auth) throws IOException {
		auth.authProcess(getClient());
	}

	public PageData load(Link link) {

		HTTPRequest getRequest = makeGetMethod(link);
		HTTPResponse response = null;
		try {
			response = getRequest.get(); // Execute the method
			int statusCode = response.getStatusCode();

			if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
				LOG.info("Content not modified since last request of " + link);
				return PageDataHttpClient.create(link, PageData.NOT_MODIFIED);
			} else if (!Status.valueOf(statusCode).isSuccess()) {
				getHttpExceptionHandler().handle(link, response, response.getStatusLine());
				return PageDataHttpClient.create(link, PageData.ERROR);
			} else if (!containsText(response)) {
				return getBinaryHandler().load(link, response);
			} else {
				CharSequence responseBody = response.getEntityAsText();
				link.setTimestamp(HttpClientUtil.getLastModified(response));
				return PageDataHttpClient.create(link, PageData.OK, responseBody, response.maybeCharSet());
			}
		} catch (IOException e) {
			LOG.warn("Failed reading from uri=" + link, e);
			return PageDataHttpClient.create(link, PageData.ERROR);
		} catch (Throwable e) {
			LOG.warn("Failed reading from uri=" + link, e);
			return PageDataHttpClient.create(link, PageData.ERROR);
		} finally {
			if (response != null)
				response.consume();
		}
	}

	private HTTPRequest makeGetMethod(Link link) {
		HTTPRequest request = super.createRequest(link);
		request.addHeader(HeaderConstants.HEADER_USER_AGENT, USER_AGENT);
		if (link.getTimestamp() > 0) {
			request.addHeader(HeaderConstants.HEADER_IF_MODIFIED_SINCE, DateUtils.formatDate(GregorianCalendar.getInstance().getTime()));
		}

		return request;
	}

	public void setBinaryHandler(BinaryHandler handler) {
		if (handler == null)
			return;
		this.binaryHandler = handler;
	}

	private BinaryHandler getBinaryHandler() {
		return binaryHandler;
	}

	public void setHttpExceptionHandler(HttpExceptionHandler handler) {
		if (handler == null)
			return;
		this.exceptionHandler = handler;
	}

	private HttpExceptionHandler getHttpExceptionHandler() {
		return exceptionHandler;
	}

	private boolean containsText(HTTPResponse response) {
		Header contentType = response.getFirstHeader(HeaderConstants.HEADER_CONTENT_TYPE);
		if (contentType != null) {
			HeaderElement[] elements = contentType.getElements();
			for (int i = 0; i < elements.length; i++) {
				String name = elements[i].getName();
				if ((name != null) && (name.startsWith("text") || name.endsWith("/xml"))) {
					return true;
				}
			}
			// if no correct content-type is found, so it isn't text
			return false;
		}
		// if no content type is set, it may be text
		return getBinaryHandler().isTextWhenNoContentType(response.getPath());
	}

	public Collection<Link> parse(PageData pageData) {
		if (!(pageData instanceof PageDataHttpClient)) {
			LOG.warn("Type mismatch in " + this.getClass().getName());
			return Collections.EMPTY_LIST;
		}
		if (pageData.getStatus() == PageData.REDIRECT) {
			Collection<Link> links = new HashSet<Link>();
			links.add(Link.create(pageData.getLink().getReferer(), pageData.getData().toString(), null, "redirect"));
			return links;
		}
		if (pageData.getDataType() == DataType.BINARY) {
			return Collections.EMPTY_SET;
		}

		return linkExtractor.retrieveURIs(pageData.getLink(), (CharSequence) pageData.getData());
	}

	public ILinkExtractor getLinkExtractor() {
		return linkExtractor;
	}

	public void setLinkExtractor(ILinkExtractor linkExtractor) {
		if (linkExtractor == null) {
			throw new IllegalArgumentException("Parameter linkExtractor is null.");
		}
		this.linkExtractor = linkExtractor;
	}

	public void shutdown() {
		getClient().shutdown();
	}


}
