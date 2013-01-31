package net.ion.crawler.handler;

import java.io.IOException;

import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.link.Link;
import net.ion.crawler.parser.PageData;
import net.ion.crawler.parser.httpclient.PageDataHttpClient;
import net.ion.framework.util.Debug;

public interface BinaryHandler {

	static BinaryHandler ABORT_HANDLER = new BinaryHandler() {
		public PageData load(Link link, HTTPResponse response) {
//			Header header = httpGet.getResponseHeader("Content-Length") ;
//			Debug.line(link.getURI(), header.getValue()) ;
			
			Debug.warn("content-type of URL[" + link + "] is not registered as text or maybe is binary type. but no binary handler registered");
			response.consume() ;
			return PageDataHttpClient.create(link, PageData.NOT_HANDLE) ;
		}

		public boolean isTextWhenNoContentType(String path) {
			return false;
		}
	};


	PageData load(Link link, HTTPResponse response) throws IOException;

	boolean isTextWhenNoContentType(String path);

}
