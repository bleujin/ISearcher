package net.ion.isearcher.crawler.filter;

import net.ion.isearcher.crawler.link.Link;
import junit.framework.TestCase;

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
