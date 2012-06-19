package net.ion.isearcher.crawler.parser.link;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import net.ion.framework.parse.html.GeneralParser;
import net.ion.framework.parse.html.HTag;
import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.crawler.filter.FileExtensionFilter;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.parser.httpclient.DetectEncodingInputStream;
import net.ion.isearcher.crawler.parser.link.LinkExtractorBleujin;

import org.apache.commons.io.IOUtils;

public class LinkExtractorBleujinTest extends ISTestCase{

	private LinkExtractorBleujin ex = new LinkExtractorBleujin() ;
	
	public void testBuilderAddNull() throws Exception {
		StringBuilder b = new StringBuilder() ;
		b.append("a") ;
		assertEquals("a", b.toString()) ;
		
		b.append((String)null) ;
		assertEquals("anull", b.toString()) ;
	}
	
	public void testDefault() throws Exception {
		Collection<Link> col = ex.retrieveURIs(Link.createStart("http://a.b.c/dir/"), "<A href='../index.htm'>anchor</a>") ;
		
		assertEquals(1, col.size()) ;
		assertEquals("http://a.b.c/index.htm", col.toArray(new Link[0])[0].getURI()) ;
	}
	
	
	public void testDual() throws Exception {
		Collection<Link> col = ex.retrieveURIs(Link.createStart("http://a.b.c/"), "<a href='/index.htm'>anchor</a>") ;
		
		assertEquals(1, col.size()) ;
		assertEquals("http://a.b.c/index.htm", col.toArray(new Link[0])[0].getURI()) ;
	}
	
	public void testExtract() throws Exception {
		Set<Link> col = ex.retrieveLinks(Link.createStart("http://a.b.c/"), "<a href='/index.htm'>anchor</a>") ;
		
		assertEquals(1, col.size()) ;
		assertEquals("http://a.b.c/index.htm", col.toArray(new Link[0])[0].getURI()) ;
		assertEquals("anchor", col.toArray(new Link[0])[0].getAnchor()) ;
	}
	

	public void testExtractImg() throws Exception {
		Set<Link> col = ex.retrieveLinks(Link.createStart("http://a.b.c/"), "<a href='/index.htm'>1<img src='abc.jpg' ALT='anchor'></a>") ;
		
		assertEquals(2, col.size()) ;
		assertEquals("http://a.b.c/index.htm", col.toArray(new Link[0])[1].getURI()) ;
		assertEquals("1 anchor", col.toArray(new Link[0])[1].getAnchor()) ;
	}
	
	public void testDetect() throws Exception {
		String file = "data/download/index.html";
		Reader reader1 = new DetectEncodingInputStream(new FileInputStream(new File(file)), 8192, Locale.getDefault()).getReader();
		Reader reader2 = new DetectEncodingInputStream(new FileInputStream(new File(file)), 4096, Locale.getDefault()).getReader();
		
		String str1 = IOUtils.toString(reader1) ;
		String str2 = IOUtils.toString(reader2) ;
		
		Debug.line(str1) ;
		//Debug.line(str2) ;
		
		assertEquals(str1, str2) ;
	}
	
	public void testLink() throws Exception {
		String file = "data/download/index.html";
		Reader reader = new DetectEncodingInputStream(new FileInputStream(new File(file)), 8192, Locale.getDefault()).getReader();

		LinkExtractorBleujin extr = new LinkExtractorBleujin();
		FileExtensionFilter filter = new FileExtensionFilter(new String[] { ".html", ".htm", "/", ".css" });
		Collection<Link> links = extr.retrieveLinks(Link.createStart("http://www.i-on.net/company/history/2009/"), reader);
		
		int count = 0 ;
		for (Link link : links) {
			Debug.debug(link.toFullString());
			if (filter.accept(Link.test("http://www.i-on.net/company/history/2009/", link.getURI()))) count++ ;
		}
		
		assertEquals(99, count) ;
		
	}
	public void testHead() throws Exception {
		String illegalHTML = "<head>" +
				"<title>..</title>" +
				"<meta http-equiv='refresh' content='0;url=http://www.i-on.net/product/ids2/overview/ids2.html'>" +
				"<meta http-equiv='Content-Type' content='text/html; charset=EUC-KR'>" +
				"</head>" +
				"<html>" +
				"<body bgcolor='white' text='black' link='blue' vlink='purple' alink='red'>" +
				"</body>" +
				"</html>" ;
		HTag root = GeneralParser.parseHTML(new StringReader(illegalHTML)) ;
		assertEquals("white", root.getChild("body").getAttributeValue("bgcolor")) ;
		
		LinkExtractorBleujin bleujin = new LinkExtractorBleujin();
		Collection<Link> links = bleujin.retrieveURIs(Link.createStart("http://blank/"), illegalHTML) ;

		Debug.debug(links) ;
		
		assertEquals(1, links.size()) ;
		assertEquals("http://www.i-on.net/product/ids2/overview/ids2.html", links.toArray(new Link[0])[0].getURI()) ;
		
		// Debug.debug(root.getChild("head/title").getTrimText()) ;
	}
	
	
}
