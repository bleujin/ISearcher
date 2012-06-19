package net.ion.isearcher;

import net.ion.framework.util.Debug;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.NonBlockingListener;
import net.ion.isearcher.indexer.collect.FileCollector;
import net.ion.isearcher.indexer.report.DefaultReporter;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.SearchResponse;
import net.ion.isearcher.searcher.processor.StdOutProcessor;

import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

public class TestCentral extends ISTestCase{

	public void testMakeSearcher() throws Exception {
		Directory dir = writeDocument() ;
		Central central = Central.createOrGet(dir) ;
		
		ISearcher searcher = central.newSearcher() ;
		searcher.addPostListener(new StdOutProcessor()) ;
		
		searcher.searchTest("bleujin") ;
	}
	
	public void testThread() throws Exception {
		Directory dir = writeDocument() ;
		Central central = Central.createOrGet(dir) ;
		
		StdOutProcessor confirmProcessor = new StdOutProcessor();

		// begin search 1
		ISearcher s1 = central.newSearcher() ;
		s1.addPostListener(confirmProcessor) ;
		SearchResponse sr = s1.search(SearchRequest.test("index.html", "name desc")) ;
		assertEquals(true, sr.getTotalCount() == 0) ;
		assertEquals(true, confirmProcessor.getTotalCount() == 0) ;
		
		// indexing..
		IWriter defaultWriter = central.testIndexer(new KoreanAnalyzer()) ;
		FileCollector col = new FileCollector(getTestDirFile(), true);
		NonBlockingListener indexListener = getAdapterListener(defaultWriter);

		col.addListener(indexListener) ;
		DefaultReporter reporter = new DefaultReporter(false);
		col.addListener(reporter) ;
		
		Thread first = new Thread(col) ; 
		first.start() ;
		
		// after search 2
		ISearcher s2 = central.newSearcher() ;
		s2.addPostListener(confirmProcessor) ;
		s2.search(createSearchRequest("index.html")) ;
		assertEquals(true, confirmProcessor.getTotalCount() == 0) ;

		indexListener.joinIndexer() ; // first index end ;


		Debug.debug("Reporter Count", reporter.getCount()) ;
		
		// after search 3
		ISearcher s3 = central.newSearcher() ;
		s3.addPostListener(confirmProcessor) ;
		s3.search(createSearchRequest("index.html")) ;
		assertEquals(true, confirmProcessor.getTotalCount() > 0) ;
		first.join() ;
	}
	
	public void testWriteMulti() throws Exception {
		Directory dir = writeDocument() ;
		Central central = Central.createOrGet(dir) ;
		StdOutProcessor confirmProcessor = new StdOutProcessor();

		// first indexing..
		FileCollector col = new FileCollector(getTestDirFile(), true);
		IWriter firstWriter = central.testIndexer(new KoreanAnalyzer()) ;
		IWriter secondWriter = central.testIndexer(new KoreanAnalyzer()) ;
		// LockObtainFaile Exception 

		NonBlockingListener adapterListener = getAdapterListener(firstWriter);
		col.addListener(adapterListener) ;
		col.addListener(new DefaultReporter(false)) ;
		Thread first = new Thread(col, "FIRST") ;
		first.start() ;
		
		
		Thread.sleep(50) ;
		// second -> write conflict ;
		try {
			FileCollector col2 = new FileCollector(getTestDirFile(), true);
			NonBlockingListener secondListener = getAdapterListener(secondWriter);
			col2.addListener(secondListener) ;
			col2.addListener(new DefaultReporter(false)) ;
			col2.collect() ;
			// fail();  //... collect  index listener(Other thread) ;  
		} catch(LockObtainFailedException ignore){
			Debug.line('=', ignore) ;
		}

		adapterListener.joinIndexer() ; // first index end ;

		ISearcher thirdSearcher = central.newSearcher() ;
		thirdSearcher.addPostListener(confirmProcessor) ;
		thirdSearcher.search(createSearchRequest("index.html")) ;
		assertEquals(true, confirmProcessor.getTotalCount() > 0) ;
		

		FileCollector col2 = new FileCollector(getTestDirFile(), true);
		NonBlockingListener secondListener = getAdapterListener(secondWriter);
		col2.addListener(secondListener) ;
		col2.addListener(new DefaultReporter(false)) ;
		col2.collect() ;
		secondListener.joinIndexer() ; // second index end ;
	}

}
