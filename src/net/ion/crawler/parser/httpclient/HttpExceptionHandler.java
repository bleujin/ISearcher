package net.ion.crawler.parser.httpclient;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.link.Link;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface HttpExceptionHandler {


	public final static HttpExceptionHandler DEFAULT = new HttpExceptionHandler() {
		private List<ExceptionInfo> store = new ArrayList<ExceptionInfo>();
		public transient Log logger = LogFactory.getLog(SimpleHttpClientParser.class);
		public void handle(Link link, HTTPResponse response, String message) {
			logger.info("Method failed: " + response.getStatusLine() + " for " + link);
			store.add(new ExceptionInfo(link, message, response.getStatusLine())) ;
		}
		public void resultToWrite(Writer rw) throws IOException {
			for (ExceptionInfo info : store) {
				rw.write("uri[" + info.link + "] : " + info.message + ", " + info.statusText + "{from " + info.link.getReferer() + "}"+ "\n") ;
			}
		}
	};

	void handle(Link link, HTTPResponse response, String message);

	void resultToWrite(Writer rw) throws IOException;
}

class ExceptionInfo {

	Link link ;
	String message ;
	String statusText ;
	public ExceptionInfo(Link link, String message, String statusText) {
		this.link = link ;
		this.message = message ;
		this.statusText = statusText ; 
	}
	
	
}
