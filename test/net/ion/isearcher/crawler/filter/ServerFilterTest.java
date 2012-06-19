package net.ion.isearcher.crawler.filter;

import net.ion.isearcher.crawler.link.Link;
import junit.framework.TestCase;

public class ServerFilterTest extends TestCase {

	private String host = "http://www.i-on.net";

	
	public void testApplication() throws Exception {
		ServerFilter rootFilter = new ServerFilter("/apps");

		
		assertEquals(true, rootFilter.accept(createLink(host, "/apps")));
		assertEquals(true, rootFilter.accept(createLink(host, "/apps/")));
		assertEquals(true, rootFilter.accept(createLink(host, "/apps/a.htm")));
	}
	
	public void testOtherHost() throws Exception {
		ServerFilter rootFilter = new ServerFilter("/");

		assertEquals(false, rootFilter.accept(createLink("http://other.i-on.net/", "http://other.i-on.net/other.htm")));
		
	}
	private Link createLink(String origin, String uri) {
		return Link.test(origin, uri);
	}
	
}
