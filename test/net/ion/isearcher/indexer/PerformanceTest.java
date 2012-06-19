package net.ion.isearcher.indexer;

import java.io.File;

import net.ion.framework.db.DBController;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.manager.OracleDBManager;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.crawler.listener.CountListener;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.collect.DatabaseCollector;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.SearchResponse;
import net.ion.isearcher.searcher.processor.StdOutProcessor;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class PerformanceTest extends ISTestCase{

	private File dataFile = new File("d:/data/");
	public void testIndex() throws Exception {
		Directory dir = FSDirectory.open(dataFile) ;
		Central central = Central.createOrGet(dir) ;
		
		IWriter newIndexer = central.testIndexer(getAnalyzer());

		NonBlockingListener ilistener = getAdapterListener(newIndexer) ;
		
		DBManager dbm = new OracleDBManager("jdbc:oracle:thin:@dev-test.i-on.net:1521:devTest", "bleu", "redf") ;
		DBController dc = new DBController(dbm) ;
		dc.initSelf() ;
		
		// 194951 unit
		IUserCommand cmd = dc.createUserCommand("select * from ics5_pia.article_tblc") ;
		DatabaseCollector dbc = new DatabaseCollector(cmd, "artid") ;
		dbc.addListener(new CountListener()) ;
		dbc.addListener(ilistener) ;
		
		// start
		dbc.collect() ;
		ilistener.joinIndexer() ;
		
		
		// end
		central.destroySelf() ;
		dc.destroySelf() ;
	}
	
	public void testSearch() throws Exception {
		Directory dir = FSDirectory.open(dataFile) ;
		Central central = Central.createOrGet(dir) ;
		ISearcher searcher = central.newSearcher() ;
		
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.searchTest("2271824") ;
	}
	
	public void testRuntime() throws Exception {
		Runtime runtime = Runtime.getRuntime();
		Debug.debug("freeMemory", runtime.freeMemory(), runtime.maxMemory()) ;
		 
	}
	
}
