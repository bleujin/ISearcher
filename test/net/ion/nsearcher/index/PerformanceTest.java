package net.ion.nsearcher.index;

import java.io.File;

import net.ion.crawler.listener.CountListener;
import net.ion.framework.db.DBController;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.manager.OracleDBManager;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.Searcher;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.collect.DatabaseCollector;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.processor.StdOutProcessor;

public class PerformanceTest extends ISTestCase{

	private File dataFile = new File("d:/data/");
	public void testIndex() throws Exception {
		Central central = CentralConfig.newLocalFile().dirFile(dataFile).build() ;
		
		NonBlockingListener ilistener = getNonBlockingListener(central.newIndexer()) ;
		
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
		ilistener.waitForCompleted() ;
		
		
		// end
		central.destroySelf() ;
		dc.destroySelf() ;
	}
	
	public void testSearch() throws Exception {
		Central central = CentralConfig.newLocalFile().dirFile(dataFile).build() ;
		Searcher searcher = central.newSearcher() ;
		
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.searchTest("2271824") ;
	}
	
	public void testRuntime() throws Exception {
		Runtime runtime = Runtime.getRuntime();
		Debug.debug("freeMemory", runtime.freeMemory(), runtime.maxMemory()) ;
		 
	}
	
}
