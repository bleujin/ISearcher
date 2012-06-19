package net.ion.isearcher.crawler.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeBuilder;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.parser.PageData;
import net.ion.isearcher.crawler.parser.PageData.DataType;
import net.ion.isearcher.crawler.parser.httpclient.PageDataHttpClient;
import net.ion.isearcher.events.ICollectorEvent;
import net.ion.isearcher.http.HTTPResponse;
import net.ion.isearcher.indexer.report.ICollectListener;

import org.apache.http.HttpStatus;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

public class BinaryParserHandler implements BinaryHandler {

	private final String extentions;
	private final String hostAddress;
	private final int limitKb;
	private final Client client;
	private AtomicInteger tryConnectCount = new AtomicInteger(0);
	private final static int MAX_TRY_COUNT = 10 ;

	public BinaryParserHandler(String extentions, String hostAddress, int limitKb) {
		this.extentions = new String("," + extentions + ",").toLowerCase();
		this.hostAddress = hostAddress;
		this.limitKb = limitKb;
		client = new Client(Protocol.HTTP);
	}

	public boolean isTextWhenNoContentType(String path) {
		return false;
	}

	public PageData load(Link link, HTTPResponse response) throws IOException {
		String path = link.getURI();
		String findExt = new String("," + StringUtil.substringAfterLast(path, ".") + ",").toLowerCase();
		if (extentions.indexOf(findExt) == -1 || isLimitSizeOver(response)) {
			return PageDataHttpClient.create(link, PageData.NOT_HANDLE);
		}
		if (tryConnectCount.intValue() >= MAX_TRY_COUNT){
			return PageDataHttpClient.create(link, PageData.ERROR) ;
		}

		Request request = new Request(Method.POST, hostAddress);
		InputStream input = response.getStream();
		try {
			request.setEntity(new InputRepresentation(input));
			Response textResponse = client.handle(request);
			Representation output = textResponse.getEntity();
			if (output == null) throw new IOException("not connected : " + hostAddress) ;
			
			Rope rope = RopeBuilder.build(new InputStreamReader(output.getStream(), "UTF-8"));
			PageData result = PageDataHttpClient.create(link, HttpStatus.SC_OK, rope, "UTF-8");
			result.setDataType(DataType.BINARY);
			
			Debug.debug(rope) ;
			return result;
		} catch (IOException ex) {
			tryConnectCount.incrementAndGet()  ;
			throw ex;
		} finally {
			input.close() ;
			response.consume() ;
		}
	}

	private boolean isLimitSizeOver(HTTPResponse response) {
		String length = response.getFirstHeader(HeaderConstants.HEADER_CONTENT_LENGTH).getValue();
		long kbLength = -1;
		if (StringUtil.isNotBlank(length)) {
			kbLength = Long.parseLong(length) / 1024;
		}
		return kbLength > this.limitKb;
	}

	public ICollectListener getEndListener() {
		return new ICollectListener() {
			public void collected(ICollectorEvent event) {
				if (event.getEventType().isEnd()) {
					try {
						tryConnectCount = new AtomicInteger(0) ;
						client.stop();
					} catch (Exception ignore) {
						ignore.printStackTrace();
					}
				}
			}
		};
	}

}
