package net.ion.isearcher.crawler.parser.httpclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Map;

import net.ion.framework.util.IOUtil;
import net.ion.isearcher.crawler.parser.PageData;
import net.ion.isearcher.crawler.util.FileUtil;
import net.ion.isearcher.crawler.util.UriFileSystemMapperUtil;
import net.ion.isearcher.http.HTTPRequest;
import net.ion.isearcher.http.HTTPResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.impl.cookie.DateUtils;
import org.restlet.engine.header.HeaderConstants;

public class DownloadHelper extends HttpClientWrapper {

	private static final transient Log LOG = LogFactory.getLog(DownloadHelper.class);

	private UriFileSystemMapperUtil mappingUtil;

	public DownloadHelper(Map mapping) {
		this(mapping, false);
	}

	public DownloadHelper(Map mapping, boolean multiThreaded) {
		super(multiThreaded);
		mappingUtil = new UriFileSystemMapperUtil(mapping);
	}

	public int download(String uri, String filename, long ifModifiedSince) {
		LOG.info("downloading '" + uri + "' to file '" + filename + '\'');

		final File file = new File(filename);
		FileUtil.createDirectoryForFile(file);

		// Create a method instance.
		HTTPRequest request = getClient().createRequest(uri) ;
		request.addHeader(HeaderConstants.HEADER_USER_AGENT, SimpleHttpClientParser.USER_AGENT);

		if (ifModifiedSince > 0) {
			request.addHeader(HeaderConstants.HEADER_IF_MODIFIED_SINCE, DateUtils.formatDate(GregorianCalendar.getInstance().getTime()));
		}

		HTTPResponse response = null ; 
		try {
			response = request.get() ;
			int statusCode = response.getStatusCode() ;
			
			if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
				LOG.info("Content not modified since last request of " + uri);
				response.consume() ;
				return PageData.NOT_MODIFIED;
			} else if (statusCode != HttpStatus.SC_OK) {
				LOG.info("Download failed '" + response.getStatusLine() + "' for URI=" + uri);
				response.consume() ;
				return PageData.ERROR;
			}
			// read the response body as a stream
			IOUtil.copyNClose(response.getStream(), new FileOutputStream(file)) ;
		} catch (IOException e) {
			LOG.warn("Failed to download URI='" + uri + "'", e);
			return PageData.ERROR;
		} finally {
			if (response != null) response.consume() ;
		}

		return PageData.OK;
	}

	public boolean download(String uri, String filename) {
		return download(uri, filename, -1L) == PageData.OK;
	}

	public int download(String uri, long ifModifiedSince) {
		String dest = getDestination(uri);
		if (dest != null) {
			return download(uri, dest, ifModifiedSince);
		} else {
			LOG.warn("DownloadHelper: no file destination found for URI=" + uri);
			return PageData.ERROR;
		}
	}

	public boolean download(String uri) {
		return download(uri, -1L) == PageData.OK;
	}

	public String getDestination(String uri) {
		return mappingUtil.getDestination(uri);
	}

}
