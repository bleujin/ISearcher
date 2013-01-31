package net.ion.crawler.filter;

import junit.framework.TestCase;
import net.ion.crawler.link.Link;

public class BeginningPathFilterTest extends TestCase{
	
	private String host = "www.i-on.net";

	public void testApplication() throws Exception {
		BeginningPathFilter rootFilter = new BeginningPathFilter("/apps");

		
		
		assertEquals(true, rootFilter.accept(createLink(host, "/apps")));
		assertEquals(true, rootFilter.accept(createLink(host, "/apps/")));
		assertEquals(true, rootFilter.accept(createLink(host, "/apps/a.htm")));
	}
	
	public void testApplicationEndDir() throws Exception {
		BeginningPathFilter rootFilter = new BeginningPathFilter("/apps/");


		assertEquals(false, rootFilter.accept(createLink(host, "/apps")));
		
		assertEquals(true, rootFilter.accept(createLink(host, "/apps/")));
		assertEquals(true, rootFilter.accept(createLink(host, "/apps/a.htm")));
	}
	
	private Link createLink(String origin, String uri) {
		return Link.test(origin, uri);
	}

}
