package net.ion.nsearcher.central;

import java.io.IOException;
import java.util.concurrent.Future;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.Searcher;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.index.NonBlockingListener;
import net.ion.nsearcher.index.collect.FileCollector;
import net.ion.nsearcher.index.report.DefaultReporter;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;
import net.ion.nsearcher.search.processor.StdOutProcessor;

import org.apache.lucene.analysis.Analyzer;

public class TestCentral extends ISTestCase{

	public void testCopy() throws Exception {
		final Central cs = CentralConfig.newRam().build() ;
		Central ct = CentralConfig.newRam().build() ;
		
		Analyzer anal = new MyKoreanAnalyzer() ;

		Future<Boolean> f1 = cs.newIndexer().asyncIndex("hero", anal, new MyIndexJob("hero"));
		f1.get() ;
		
		Future<Boolean> f2 = ct.newIndexer().asyncIndex("bleujin", anal, new MyIndexJob("bleujin"));
		f2.get() ;
		
		ct.newIndexer().index(new MyKoreanAnalyzer(), new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.appendFrom(cs.dir()) ;
				return null;
			}
		}) ;
		assertEquals(10, ct.newSearcher().searchTest("").getTotalCount()) ;
	}
	
	
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

class MyIndexJob implements IndexJob<Boolean> {
	String name ;
	MyIndexJob(String name){
		this.name = name ;
	}
	
	public Boolean handle(IndexSession session) throws IOException {
		for (int i : ListUtil.rangeNum(5)) {
			session.insertDocument(MyDocument.testDocument().add(MyField.number("index", i)).add(MyField.keyword("name", name))) ;
		}
		return true;
	}
}
