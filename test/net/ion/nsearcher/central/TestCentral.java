package net.ion.nsearcher.central;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.Searcher;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.index.NonBlockingListener;
import net.ion.nsearcher.index.collect.FileCollector;
import net.ion.nsearcher.index.report.DefaultReporter;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.processor.StdOutProcessor;

import org.apache.lucene.store.LockObtainFailedException;

public class TestCentral extends ISTestCase{

	public void testMakeSearcher() throws Exception {
		Central central = writeDocument() ;
		
		Searcher searcher = central.newSearcher() ;
		searcher.addPostListener(new StdOutProcessor()) ;
		searcher.searchTest("bleujin") ;
	}
	
	public void testThread() throws Exception {
		Central central = writeDocument() ;
		
		StdOutProcessor confirmProcessor = new StdOutProcessor();

		// begin search 1
		Searcher searcher1 = central.newSearcher() ;
		searcher1.addPostListener(confirmProcessor) ;
		SearchResponse sr = searcher1.search(SearchRequest.create("041820").descending("name")) ;
		sr.awaitPostFuture()  ; 
		
		assertEquals(true, sr.getTotalCount() == 0) ;
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
		s2.search(createSearchRequest("041820")) ;
		assertEquals(true, confirmProcessor.getTotalCount() == 0) ;

		indexListener.waitForCompleted() ; // first index end ;


		Debug.debug("Reporter Count", reporter.getCount()) ;
		
		// after search 3
		Searcher s3 = central.newSearcher() ;
		s3.addPostListener(confirmProcessor) ;
		
		s3.search(SearchRequest.ALL) ;
		
		final SearchResponse response = s3.search(createSearchRequest("tdump"));
		response.awaitPostFuture() ;
		assertEquals(true, confirmProcessor.getTotalCount() > 0) ;
		first.join() ;
	}
	
	public void testWriteMulti() throws Exception {
		Central central = writeDocument() ;
		StdOutProcessor confirmProcessor = new StdOutProcessor();

		// first indexing..
		FileCollector col = new FileCollector(getTestDirFile(), true);
		NonBlockingListener indexListener = getNonBlockingListener(central.newIndexer());
		col.addListener(indexListener) ;
		col.addListener(new DefaultReporter(false)) ;
		new Thread(col, "FIRST").start() ;

		Thread.sleep(50) ;

		Indexer secondWriter = central.newIndexer() ;
		FileCollector col2 = new FileCollector(getTestDirFile(), true);
		NonBlockingListener secondListener = getNonBlockingListener(secondWriter);
		col2.addListener(secondListener) ;
		col2.addListener(new DefaultReporter(false)) ;
		col2.collect() ;

		
		indexListener.waitForCompleted() ; // first index end ;

		Searcher searcher = central.newSearcher() ;
		searcher.addPostListener(confirmProcessor) ;
		final SearchResponse response = searcher.search(createSearchRequest("tdump"));

		response.awaitPostFuture() ;
		
		
		assertEquals(true, confirmProcessor.getTotalCount() > 0) ;
		
	}

}
