package net.ion.isearcher.crawler.filter;

import net.ion.isearcher.crawler.link.Link;
import junit.framework.TestCase;

public class FileExtensionFilterTest extends TestCase{

	private FileExtensionFilter filter = new FileExtensionFilter(new String[]{".html", ".htm"}) ;
	String host = "http://ion.net";

	
	public void testHTML() throws Exception {
		assertTrue(filter.accept(createLink(host, "/a.htm")))  ;
		assertTrue(filter.accept(createLink(host, "/a.html")))  ;
	}
	
	public void testOther() throws Exception {
		assertFalse(filter.accept(createLink(host, "/a.jpg")))  ;
		assertFalse(filter.accept(createLink(host, "/a.gif")))  ;
		assertFalse(filter.accept(createLink(host, "/a.htm2")))  ;
		assertFalse(filter.accept(createLink(host, "/a.php")))  ;
	}
	
	
	public void testCase() throws Exception {
		assertTrue(filter.accept(createLink(host, "/a.HTM")))  ;
	}
	
	
	public void testParameter() throws Exception {
		assertFalse(filter.accept(createLink(host, "/a.jpg?file=a_htm")))  ;
		assertFalse(filter.accept(createLink(host, "/a_htm")))  ;
	}
	private Link createLink(String origin, String uri) {
		return Link.test(origin, uri);
	}

}
