package net.ion.isearcher.crawler;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeWriter;
import net.ion.framework.util.Debug;
import net.ion.isearcher.crawler.core.AbstractCrawler;
import net.ion.isearcher.crawler.core.ICrawler;
import net.ion.isearcher.crawler.filter.ILinkFilter;
import net.ion.isearcher.crawler.filter.ServerFilter;
import net.ion.isearcher.crawler.handler.BinaryParserHandler;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.link.LinkGraph;
import net.ion.isearcher.crawler.listener.CountListener;
import net.ion.isearcher.crawler.listener.DebugListener;
import net.ion.isearcher.crawler.lucene.LuceneHTMLDocumentParserEventListener;
import net.ion.isearcher.crawler.model.MaxDepthModel;
import net.ion.isearcher.crawler.model.MaxIterationsModel;
import net.ion.isearcher.crawler.parser.httpclient.SimpleHttpClientParser;
import net.ion.isearcher.events.LinkGraphParserEventListener;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class CrawlSiteTest extends TestCase{

	
	public void testSampleSitteMaxDepthModel() throws Exception {
		int depth = 1;
		String server = "http://golf.sbs.co.kr/";
		String start = "index.html";

		Crawler crawler = new Crawler();
		SimpleHttpClientParser parser = new SimpleHttpClientParser(true);
		crawler.setParser(parser);
		// ILinkFilter filter = LinkFilterUtil.and(new ServerFilter(server), new FileExtensionFilter(new String[]{".htm", ".html"})) ;

		crawler.setLinkFilter(new ServerFilter(server));
		crawler.setModel(new MaxDepthModel(depth));

		// path check
		LinkGraphParserEventListener graph = new LinkGraphParserEventListener();
		DebugListener debug = new DebugListener();
		crawler.addListener(graph) ;
		crawler.addListener(debug) ;

		// start crawler
		crawler.setStartPage(server, start);
		crawler.collect() ;
		
		Debug.debug(makeGraph(crawler, graph)) ;
		Debug.debug(debug.resultReport()) ;
		
	}
	
	public void testSampleSiteIterationModel() throws Exception {
		int iterCount = 10;
		String server = "http://localhost:8095/";
		String start = "simple/index.htm";

		Crawler crawler = new Crawler();
		SimpleHttpClientParser parser = new SimpleHttpClientParser(true);
		crawler.setParser(parser);
		// ILinkFilter filter = LinkFilterUtil.and(new ServerFilter(server), new FileExtensionFilter(new String[]{".htm", ".html"})) ;
		ILinkFilter filter = new ServerFilter(server);

		crawler.setLinkFilter(filter);
		crawler.setModel(new MaxIterationsModel(iterCount));

		// path check
		LinkGraphParserEventListener graph = new LinkGraphParserEventListener();
		crawler.addListener(graph) ;
		crawler.addListener(new DebugListener()) ;

		// 
		// Map mapping = new HashMap();
		// mapping.put(server, "c:/temp/ion_page/");
		// crawler.addParserListener(new DownloadEventListener(mapping)) ;

		// start crawler
		crawler.setStartPage(server, start);
		crawler.collect() ;
		
		Debug.debug(makeGraph(crawler, graph)) ;
	}
	
	
	public void testSampleSiteBinary() throws Exception {
		int depth = 3;
		String server = "http://localhost:8095/simple/";
		String start = "index.htm";

		MultiThreadedCrawler crawler = new MultiThreadedCrawler();
		SimpleHttpClientParser parser = new SimpleHttpClientParser(true);
		BinaryParserHandler bhandler = new BinaryParserHandler("doc,docx,pdf", "http://localhost:8182/extractor/icss/stream", 2000);
		parser.setBinaryHandler(bhandler) ;
		crawler.setParser(parser);
		// ILinkFilter filter = LinkFilterUtil.and(new ServerFilter(server), new FileExtensionFilter(new String[]{".htm", ".html"})) ;
		ILinkFilter filter = new ServerFilter(server);

		crawler.setLinkFilter(filter);
		crawler.setModel(new MaxDepthModel(depth));

		// path check
		LinkGraphParserEventListener graph = new LinkGraphParserEventListener();
		crawler.addListener(graph) ;
		crawler.addListener(new DebugListener()) ;
		crawler.addListener(bhandler.getEndListener()) ;

		// start crawler
		crawler.setStartPage(server, start);
		crawler.collect() ;
		
		Debug.debug(makeGraph(crawler, graph)) ;
	}
	
	
	public void testION() throws Exception {
		int depth = 0;
		String server = "http://www.i-on.net/";
		String start = "/index.html";

		Crawler crawler = new Crawler();
		SimpleHttpClientParser parser = new SimpleHttpClientParser(true);

		// NameValuePair[] nameValuePairs = new NameValuePair[] {new NameValuePair("inid","iihi"), new NameValuePair("inpasswd","dkdldhs"), new
		// NameValuePair("gotourl","")};
		// CookieAuth cookieAuth = new CookieAuth(new LoginSetting("http://cert.golf.sbs.co.kr/html/front/login/lg_login.jsp", nameValuePairs)) ;
		// crawler.setAuth(cookieAuth) ;

		crawler.setParser(parser);
		// ILinkFilter filter = LinkFilterUtil.and(new ServerFilter(server), new FileExtensionFilter(new String[]{".htm", ".html"})) ;
		ILinkFilter filter = new ServerFilter(server);

		crawler.setLinkFilter(filter);
		crawler.setModel(new MaxDepthModel(3));

		// path check
		LinkGraphParserEventListener graph = new LinkGraphParserEventListener();
		crawler.addParserListener(new CountListener(100));
		crawler.addListener(graph) ;

		// 
		// Map mapping = new HashMap();
		// mapping.put(server, "c:/temp/ion_page/");
		// crawler.addParserListener(new DownloadEventListener(mapping)) ;

		// start crawler
		crawler.setStartPage(server, start);
		crawler.collect() ;
		
		Debug.debug(makeGraph(crawler, graph)) ;
	}
	
	private Rope makeGraph(ICrawler crawler, LinkGraphParserEventListener graph) throws IOException {
		RopeWriter rw = new RopeWriter() ;
		rw.write("Origin = " + graph.getOrigin() + "\n");
        // statistics
        Collection<Link> visitedLinks = crawler.getModel().getVisitedURIs(); 
        rw.write("Links visited  =", String.valueOf(visitedLinks.size()), "\n");
        rw.write("Links unvisited=", String.valueOf(crawler.getModel().getToVisitURIs().size()), "\n");
        rw.write("Model =", crawler.getModel().toString(), "\n") ;
        
        // show link graph of the visited links
        for(Link link : visitedLinks) {
            LinkGraph linkGraph = graph.getLink(link);
            linkGraph.write(crawler, rw) ;
        }
        return rw.getRope() ;
	}
	
	
	public void xtestSBSGolfCrawling() throws Exception {
		int depth = 5;
		String server = "http://ko.wikipedia.org/wiki/";
		// String server = "http://www.ics.uci.edu/";
		String start = "/index.html";
		// create Lucene index writer
		// IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
		FSDirectory dir = FSDirectory.open(new File("c:/temp"));
		IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_CURRENT), true, MaxFieldLength.LIMITED);

		// common crawler settings
		// Crawler scrawler = new Crawler();
		MultiThreadedCrawler crawler = new MultiThreadedCrawler();
		SimpleHttpClientParser parser = new SimpleHttpClientParser(true);

		// NameValuePair[] nameValuePairs = new NameValuePair[] {new NameValuePair("inid","iihi"), new NameValuePair("inpasswd","dkdldhs"), new
		// NameValuePair("gotourl","")};
		// CookieAuth cookieAuth = new CookieAuth(new LoginSetting("http://cert.golf.sbs.co.kr/html/front/login/lg_login.jsp", nameValuePairs)) ;
		// crawler.setAuth(cookieAuth) ;

		crawler.setParser(parser);
		// ILinkFilter filter = LinkFilterUtil.and(new ServerFilter(server), new FileExtensionFilter(new String[]{".htm", ".html"})) ;
		ILinkFilter filter = new ServerFilter(server);

		crawler.setLinkFilter(filter);
		crawler.setModel(new MaxDepthModel(depth));

		// create Lucene parsing listener and add it
		crawler.addParserListener(new LuceneHTMLDocumentParserEventListener(writer));

		// path check
		LinkGraphParserEventListener graph = new LinkGraphParserEventListener();
		crawler.addParserListener(new CountListener(100));

		// 
		// Map mapping = new HashMap();
		// mapping.put(server, "c:/temp/ion_page/");
		// crawler.addParserListener(new DownloadEventListener(mapping)) ;

		// start crawler
		crawler.setStartPage(server, start);
		crawler.collect() ;

		// Optimizing Lucene index
		writer.optimize();
		writer.close();

		// reportPathCheckFile(crawler, graph, new PrintStream(getTestDir() + "/report.txt")) ;

		// Collection<Link> visited = crawler.getModel().getVisitedURIs() ;
		//        
		// Iterator iter = visited.iterator() ;
		// while(iter.hasNext()){
		// Debug.debug(iter.next()) ;
		// }

	}
}
