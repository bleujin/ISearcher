package net.ion.isearcher.crawler.filter;

import net.ion.isearcher.crawler.link.Link;
import junit.framework.TestCase;

public class SubDomainFilterTest extends TestCase{

	private SubDomainFilter filter = new SubDomainFilter("ion.net") ;

	private Link createLink(String origin, String uri) {
		return Link.test(origin, uri);
	}

	public void testHome() throws Exception {
		assertEquals(true, filter.accept(createLink("http://www.ion.net", "/"))) ;
		assertEquals(true, filter.accept(createLink("http://www.ion.net/", "/"))) ;
	}

	public void testOtherDomain() throws Exception {
		assertEquals(false, filter.accept(createLink("http://www.ion.com", "/"))) ;
		assertEquals(false, filter.accept(createLink("http://www.ion.co.kr/", "/"))) ;
	}

	public void testOtherDomain2() throws Exception {
		SubDomainFilter newFilter = new SubDomainFilter("http://ion.net/") ;
		
		assertEquals(true, newFilter.accept(createLink("http://www.ion.net", "/"))) ;
		assertEquals(true, newFilter.accept(createLink("http://dev.ion.net/", "/"))) ;

		assertEquals(true, newFilter.accept(createLink("http://www.ion.NET/", "/"))) ;
		assertEquals(true, newFilter.accept(createLink("http://dev.ION.net/", "/"))) ;

	}


}
