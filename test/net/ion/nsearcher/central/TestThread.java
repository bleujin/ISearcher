package net.ion.nsearcher.central;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.index.NonBlockingListener;
import net.ion.nsearcher.index.collect.FileCollector;
import net.ion.nsearcher.index.report.DefaultReporter;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.processor.StdOutProcessor;

public class TestThread extends ISTestCase{

	

	public void testThread() throws Exception {
		Central central = writeDocument() ;
		
		StdOutProcessor confirmProcessor = new StdOutProcessor();

		// begin search 1
		Searcher searcher1 = central.newSearcher() ;
		searcher1.addPostListener(confirmProcessor) ;
		SearchResponse sr = searcher1.createRequest("041820").descending("name").find() ;
		sr.awaitPostFuture()  ; // wait search listener
		
		assertEquals(true, sr.size() == 0) ;
		assertEquals(true, confirmProcessor.getTotalCount() == 0) ;

		
		// indexing..
		Indexer indexer = central.newIndexer() ;
		FileCollector col = new FileCollector(getTestDirFile(), true);
		NonBlockingListener indexListener = getNonBlockingListener(indexer);

		col.addListener(indexListener) ;
		DefaultReporter reporter = new DefaultReporter(false);
		col.addListener(reporter) ;
		
		Thread first = new Thread(col) ; 
		first.start() ;
		
		
		
		// after search 2
		Searcher s2 = central.newSearcher() ;
		s2.addPostListener(confirmProcessor) ;
		s2.search("041820") ;
		assertEquals(true, confirmProcessor.getTotalCount() == 0) ;

		indexListener.waitForCompleted() ; // first index end ;

		Debug.debug("Reporter Count", reporter.getCount()) ;
		
		// after search 3
		Searcher s3 = central.newSearcher() ;
		s3.addPostListener(confirmProcessor) ;
		
		s3.search("") ;
		
		final SearchResponse response = s3.search("tdump");
		response.awaitPostFuture() ;
		assertEquals(true, confirmProcessor.getTotalCount() > 0) ;
		
		central.close(); 
	}
	
	public void testWriteMulti() throws Exception {
		Central central = writeDocument() ;
		StdOutProcessor confirmProcessor = new StdOutProcessor();

		// first indexing..
		FileCollector col = new FileCollector(getTestDirFile(), true);
		NonBlockingListener firstListener = getNonBlockingListener(central.newIndexer());
		col.addListener(firstListener) ;
		col.addListener(new DefaultReporter(false)) ;
		new Thread(col, "FIRST").start() ;

		Thread.sleep(20) ;

		FileCollector col2 = new FileCollector(getTestDirFile(), true);
		NonBlockingListener secondListener = getNonBlockingListener(central.newIndexer() );
		col2.addListener(secondListener) ;
		col2.addListener(new DefaultReporter(false)) ;
		col2.collect() ;

		
		firstListener.waitForCompleted() ; // first index end ;

		Searcher searcher = central.newSearcher() ;
		searcher.addPostListener(confirmProcessor) ;
		final SearchResponse response = searcher.search("tdump");
		response.awaitPostFuture() ;
		
		Debug.line(confirmProcessor.getTotalCount());
		assertEquals(true, confirmProcessor.getTotalCount() > 0) ;
		
		secondListener.waitForCompleted(); 
		central.close(); 
	}
}
