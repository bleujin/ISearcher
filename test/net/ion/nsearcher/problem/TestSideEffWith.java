package net.ion.nsearcher.problem;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.NonBlockingListener;
import net.ion.nsearcher.index.collect.FileCollector;
import net.ion.nsearcher.index.report.DefaultReporter;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.processor.StdOutProcessor;

public class TestSideEffWith extends ISTestCase{

	private void makeSearcher() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		central.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				isession.insertDocument(isession.newDocument("bleujin")) ;
				return null;
			}
		}) ;
		
		
		Searcher searcher = central.newSearcher() ;
		searcher.addPostListener(new StdOutProcessor()) ;
		assertEquals(true, searcher.search("bleujin").getDocument().size() > 0) ;
		
		central.close(); 
	}
	
	
	private void writeMulti() throws Exception {
		Central central =CentralConfig.newRam().build() ;
		StdOutProcessor confirmProcessor = new StdOutProcessor();

		// first indexing..
		FileCollector col = new FileCollector(getTestDirFile(), true);
		NonBlockingListener firstListener = getNonBlockingListener(central.newIndexer());
		col.addListener(firstListener) ;
		col.addListener(new DefaultReporter(false)) ;
		new Thread(col, "FIRST").start() ;

		Thread.sleep(5) ;

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
		assertEquals(true, confirmProcessor.getTotalCount() > 0) ;  ///.....?
		
	
		secondListener.waitForCompleted();
		central.close(); 
	}
	
	public void testMulti() throws Exception {
		final TestSideEffWith ct = new TestSideEffWith() ;
		ct.makeSearcher() ;
		ct.writeMulti();
	}
	

}
