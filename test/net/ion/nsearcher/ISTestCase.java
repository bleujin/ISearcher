package net.ion.nsearcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.Logger;
import net.htmlparser.jericho.LoggerProvider;
import net.ion.crawler.core.ICrawler;
import net.ion.crawler.event.LinkGraphParserEventListener;
import net.ion.crawler.link.Link;
import net.ion.crawler.link.LinkGraph;
import net.ion.framework.db.DBController;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.manager.OracleDBManager;
import net.ion.framework.db.servant.StdOutServant;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.exception.IndexException;
import net.ion.nsearcher.index.AsyncIndexer;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.index.NonBlockingListener;
import net.ion.nsearcher.index.channel.MemoryChannel;
import net.ion.nsearcher.index.event.ICollectorEvent;
import net.ion.nsearcher.index.policy.ExceptionPolicy;
import net.ion.nsearcher.index.policy.MergePolicy;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.debug.standard.DCJKAnalyzer;
import org.apache.lucene.analysis.debug.standard.DStandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class ISTestCase extends TestCase{

	private String testDir = "resource/sample/" ;
	
	public void setUp() throws Exception {
		Config.LoggerProvider = new LoggerProvider() {
			public Logger getLogger(String arg0) {
				return new WrapperLogger();
			}
		};
	}
	
	protected File getTestDirFile(){
		return new File(testDir) ;
	}
	protected File getTestDir(String subDir){
		return new File(testDir + subDir) ;
	}

	public DBController createTestDBController() throws Exception {
		DBController sdc = new DBController("ISTestCase", createOldSearchDBManager(), new StdOutServant()) ;
		sdc.initSelf() ;
		return sdc ;
	}
	private DBManager createOldSearchDBManager() {
		DBManager dbm = new OracleDBManager("jdbc:oracle:thin:@dev-sql.i-on.net:1521:devSQL", "dev_icss5", "dev_icss5");
		return dbm;
	}

	protected void reportPathCheckFile(ICrawler crawler, LinkGraphParserEventListener graph, PrintStream out) throws FileNotFoundException {
        out.println("Origin = " + graph.getOrigin());
        // statistics
        Collection<Link> visitedLinks = crawler.getModel().getVisitedURIs(); 
        out.println("Links visited  =" + visitedLinks.size());
        out.println("Links unvisited=" + crawler.getModel().getToVisitURIs().size());
        out.println("Links in graph =" + graph.getLinks().size());
        
        // show link graph of the visited links
        Iterator<Link> list = visitedLinks.iterator();
        while (list.hasNext()) {
            Link link = (Link) list.next();
            out.println(link.getURI());

            LinkGraph linkGraph = graph.getLink(link);
            
            for (Link inLink : linkGraph.inLinks()) {
                out.println("-> in:  " + inLink);
            }

            for (Link outLink : linkGraph.outLinks()) {
                out.println("-> out: " + outLink);
            }
            out.flush() ;
        }
        out.close() ;
	}
	protected Central sampleTestDocument() throws CorruptIndexException, LockObtainFailedException, IOException, IndexException, InterruptedException, ExecutionException {
		return writeDocument(createDefaultAnalyzer()) ;
	}
	
	protected Central writeDocument(Analyzer analyzer) throws CorruptIndexException, LockObtainFailedException, IOException, IndexException, InterruptedException, ExecutionException {
		return writeDocument(CentralConfig.newRam(), analyzer) ;
	}
	
	protected Central writeDocument(CentralConfig config, Analyzer analyzer) throws CorruptIndexException, LockObtainFailedException, IOException, IndexException, InterruptedException, ExecutionException {
		Central central = config.build() ;
		Indexer indexer = central.newIndexer() ;
		
		indexer.index(analyzer, new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws IOException {
				WriteDocument[] docs = ISTestCase.makeTestMyDocument(isession,  20) ;
				for (WriteDocument doc : docs) {
					isession.insertDocument(doc) ;	
				}
				return null;
			}
		}) ;
		// writer.close() ;
		return central;
	}
	
	public static IndexWriterConfig testWriterConfig(){
		Version version = Version.LUCENE_CURRENT ;
		return new IndexWriterConfig(version, new DStandardAnalyzer(version)) ;
	}

	public static IndexWriter testWriter(Directory dir, Analyzer analyzer) throws IOException{
		Version version = Version.LUCENE_CURRENT ;
		return new IndexWriter(dir, new IndexWriterConfig(version, new DStandardAnalyzer(version))) ;
	}
	


	protected void clearWriterDic(){
		
	}
	
	protected NonBlockingListener getNonBlockingListener(Indexer iw) throws IOException {
		AsyncIndexer indexer = new AsyncIndexer(iw, createDefaultAnalyzer()) ;
		indexer.setExceptionPolicy(ExceptionPolicy.ABORT_AFTER_ROLLBACK) ;
		indexer.setWritePolicy(new MergePolicy()); // merge policy
		
		NonBlockingListener adapterListener = new NonBlockingListener(indexer, new MemoryChannel<ICollectorEvent>());
		return adapterListener;
	}
	
	public static WriteDocument[] makeTestMyDocument(IndexSession isession, int count){
		List<WriteDocument> list = new ArrayList<WriteDocument>() ;
		String[] ranName = new String[]{"bleujin", "novision", "iihi", "k2sun"} ;
		for (int j = 0; j < count; j++) {
			WriteDocument myDoc = isession.newDocument() ;
			// int : 100 - 200
			myDoc.add(MyField.number("int", 100 + RandomUtil.nextInt(100))) ;
			myDoc.add(MyField.keyword("date", DateUtils.formatDate(RandomUtil.nextCalendar(10).getTime(), "yyyyMMdd-HH24mmss"))) ;
			myDoc.add(MyField.keyword("name", ranName[j % ranName.length])) ;
			myDoc.add(MyField.text("subject", RandomStringUtils.randomAlphabetic(20))) ;
			list.add(myDoc) ;
		}
		
		WriteDocument myDoc1 = isession.newDocument() ;
		//myDoc1.add(MyField.number("int", 2)) ;
		//myDoc1.add(MyField.text("int", "2")) ;
		myDoc1.add(MyField.number("int", 3)) ;
		myDoc1.add(MyField.keyword("date", DateUtils.formatDate(RandomUtil.nextCalendar(10).getTime(), "yyyyMMdd-HH24mmss"))) ;
		myDoc1.add(MyField.keyword("name", "bleujin")) ;
		myDoc1.add(MyField.text("mysub", "bleujin novision")) ;
		myDoc1.add(MyField.text("content", RandomStringUtils.random(400, new char[]{'A','B','C','D','E', ' '}))) ;
		list.add(myDoc1) ;

		WriteDocument myDoc2 = isession.newDocument() ;
		myDoc2.add(MyField.keyword("int", "3")) ;
		myDoc2.add(MyField.number("int", 3)) ;
		myDoc2.add(MyField.number("INT", 4)) ;
		myDoc2.add(MyField.keyword("stop", "냐옹")) ;
		myDoc2.add(MyField.keyword("date", DateUtils.formatDate(RandomUtil.nextCalendar(10).getTime(), "yyyyMMdd-HH24mmss"))) ;
		myDoc2.add(MyField.keyword("name", "dup2")) ;
		myDoc2.add(MyField.keyword("ud1", "sky_earth")) ;
		myDoc2.add(MyField.text("ud2", "sky_earth")) ;
		myDoc2.add(MyField.text("subject", RandomStringUtils.randomAlphabetic(20))) ;
		myDoc2.add(MyField.text("content", RandomStringUtils.random(400, new char[]{'A','B','C','D','E', ' '}))) ;
		list.add(myDoc2) ;

		
		WriteDocument myDoc3 = isession.newDocument() ;
		myDoc3.add(MyField.number("long", 1234L)) ;
		myDoc3.add(MyField.keyword("key", "long")) ;
		myDoc3.add(MyField.keyword("name", "test")) ;
		list.add(myDoc3) ;

		WriteDocument myDoc4 = isession.newDocument() ;
		myDoc4.add(MyField.date("date", 20100725, 232010)) ;
		myDoc4.add(MyField.keyword("name", "date")) ;
		list.add(myDoc4) ;


		return (WriteDocument[])list.toArray(new WriteDocument[0]);	}
	
	
	public static WriteDocument[] makeTestDocument(IndexSession isession, int count) {
		WriteDocument[] mydocs = makeTestMyDocument(isession, count) ;
		List<WriteDocument> list = ListUtil.newList() ;
		
		for (WriteDocument mydoc : mydocs) {
			list.add(mydoc) ;
		}
		return (WriteDocument[])list.toArray(new WriteDocument[0]);
	}

	public Analyzer createDefaultAnalyzer() {
		// return new KoreanAnalyzer();
		return new DCJKAnalyzer(SearchConstant.LuceneVersion);
	}
	
	public Analyzer createKoreanAnalyzer() throws IOException {
		// return new KoreanAnalyzer();
		return new MyKoreanAnalyzer(SearchConstant.LuceneVersion);
	}
	

}

class WrapperLogger implements Logger {

	public void debug(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void error(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void info(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void warn(String arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
