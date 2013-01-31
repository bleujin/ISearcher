package net.ion.crawler.parser.link;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import net.ion.crawler.MultiThreadedCrawler;
import net.ion.crawler.filter.FileExtensionFilter;
import net.ion.crawler.filter.ILinkFilter;
import net.ion.crawler.filter.LinkFilterUtil;
import net.ion.crawler.filter.ServerFilter;
import net.ion.crawler.link.Link;
import net.ion.crawler.model.MaxDepthModel;
import net.ion.crawler.parser.httpclient.DetectEncodingInputStream;
import net.ion.crawler.parser.httpclient.SimpleHttpClientParser;
import net.ion.framework.parse.html.GeneralParser;
import net.ion.framework.parse.html.HTag;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class ExtractLinkTest extends ISTestCase {

	public void setUp() throws Exception {
		super.setUp();
	}

	
	public void testIMLink() throws Exception {
		String file = "data/download/im.htm";
		Reader reader = new DetectEncodingInputStream(new FileInputStream(new File(file))).getReader();

		LinkExtractorBleujin extr = new LinkExtractorBleujin();
		// FileExtensionFilter filter = new FileExtensionFilter(new String[] { ".html", ".htm", "/", ".css" });
		Collection<Link> links = extr.retrieveLinks(Link.createStart("http://www.i-on.net/company/history/2009/"), reader);
		
		for (Link link : links) {
			Debug.debug(link.toFullString()) ;
		}
		
		assertEquals(128, links.size()) ;
	}
	
	
	public void testBaseHref() throws Exception {
		String file = "data/download/basehref.htm";
		Reader reader = new DetectEncodingInputStream(new FileInputStream(new File(file))).getReader();

		LinkExtractorBleujin extr = new LinkExtractorBleujin();
		// FileExtensionFilter filter = new FileExtensionFilter(new String[] { ".html", ".htm", "/", ".css" });
		Collection<Link> links = extr.retrieveLinks(Link.createStart("http://dev-ibr.i-on.net:8888/ibr/main.do"), reader);
		
		for (Link link : links) {
			assertEquals(true, link.getURI().startsWith("http://dev-ibr.i-on.net:8888/ibr/")) ;
		}
	}
	
	public void testEscapeHTML() throws Exception {
		String str = "abc.php?a=abc&b=3" ;
		String esStr = StringEscapeUtils.escapeHtml(str) ;
		assertEquals(str, StringEscapeUtils.unescapeHtml(esStr) ) ;
	}
	
	
	public void testFilter() throws Exception {
		FileExtensionFilter filter = new FileExtensionFilter(".php") ;
		boolean result = filter.accept(createLink("http://im.i-on.net/zeroboard/main.php", "http://im.i-on.net/zeroboard/zboard.php?id=notice&select_arrange=headnum&desc=asc&page_num=20&selected=&exec=&sn=off&ss=on&sc=off&category=9&keyword=")) ;
		
		assertEquals(true, result) ;

		result = filter.accept(createLink("http://im.i-on.net/zeroboard/main.php", "http://im.i-on.net/zeroboard/zboard.php")) ;
		assertEquals(true, result) ;


		result = filter.accept(createLink("http://im.i-on.net/zeroboard/main.php", "")) ;
		assertEquals(false, result) ;

		result = filter.accept(createLink("http://im.i-on.net/zeroboard/main.php", "http://im.i-on.net/zeroboard/zboard.php?id=notice&select_arrange=headnum?")) ;
		assertEquals(true, result) ;

	}
	
	private Link createLink(String origin, String uri) {
		return Link.test(origin, uri);
	}

	
	public void testComment() throws Exception {
		String commentHTML =  "<!--<td><img src='http://www.i-on.net/images/main/icon_tea.gif'></td>" + 
        		"<td class='m_slide'><a href='http://www.i-on.net/product/tea/index.html'>tea</a></td>-->" ;
		
		HTag root = GeneralParser.parseHTML(new StringReader(commentHTML)) ;
		
		
		assertEquals("!--", root.getTagName()) ;
		assertEquals(true, root.getContent().startsWith("<!--<td><img src='http")) ;
		assertEquals(false, root.hasChild()) ;
		
		
		
		
		LinkExtractorBleujin bleujin = new LinkExtractorBleujin();
		Collection<Link> links = bleujin.retrieveURIs(Link.createStart("http://blank/"), commentHTML) ;
		
		Debug.debug(links.size()) ;
		
		
	}
	

	
	public void testHttptHead() throws Exception{
        int depth = 0;
        String server = "http://www.i-on.net";
        String start = "/index.html";
		// create Lucene index writer
		// IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
        FSDirectory dir = FSDirectory.open(new File("c:/temp")) ;
		IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_CURRENT), true, MaxFieldLength.LIMITED);

        // common crawler settings
        MultiThreadedCrawler crawler = new MultiThreadedCrawler() ;
		SimpleHttpClientParser parser = new SimpleHttpClientParser(true);
		parser.setLinkExtractor(new LinkExtractorBleujin()) ;
		// parser.setLinkExtractor(LinksUtil.DEFAULT_LINK_EXTRACTOR) ;
		crawler.setParser(parser) ;
        ILinkFilter filter = LinkFilterUtil.and(new ServerFilter(server), new FileExtensionFilter(new String[]{".htm", ".html", "/"})) ;
		crawler.setLinkFilter(filter);
        crawler.setModel(new MaxDepthModel(depth));

        // create Lucene parsing listener and add it
        // crawler.addParserListener(new LuceneHTMLDocumentParserEventListener(writer));
        
        // start crawler
        crawler.setStartPage(server, start);
        crawler.collect() ;
        
        // Optimizing Lucene index
        writer.close();

	}
}
