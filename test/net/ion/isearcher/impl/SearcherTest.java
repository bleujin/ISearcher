package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.indexer.NonBlockingListener;
import net.ion.isearcher.indexer.collect.FileCollector;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.SearchResponse;
import net.ion.isearcher.searcher.processor.StdOutProcessor;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class SearcherTest extends ISTestCase {

	private ISearcher searcher;
	private Central central = null ; 
	public void setUp() throws Exception {
		Directory dir = writeDocument() ;
		central = Central.createOrGet(dir) ;
		searcher = central.newSearcher() ;
	}

	public void tearDown() throws Exception {
		central.destroySelf() ;
	}


	public void testExecute() throws Exception {
		FileCollector col = new FileCollector(getTestDir("/ion_page"), true);

		NonBlockingListener adapterListener = getAdapterListener(true);
		col.addListener(adapterListener) ;
		// col.addListener(new DefaultReportor()) ;
		
		col.collect() ;
		adapterListener.joinIndexer() ;
	}


	public void testSearchCount() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		 ;
		SearchResponse result = searcher.search(SearchRequest.test("bleujin").setPage(Page.ALL));
		List<MyDocument> docs = result.getDocument();
		assertEquals(6, result.getTotalCount());
	}
	

	public void testSearchFieldCount() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		 ;
		SearchResponse result = searcher.search(SearchRequest.test("mysub:(bleujin novision) OR subject:(bleujin novision)").setPage(Page.ALL));
		List<MyDocument> docs = result.getDocument();
		for (MyDocument doc : docs) {
			Debug.line(doc) ;
		}
	
	}
	

	public void testPage() throws Exception {
		SearchResponse result = searcher.search(SearchRequest.test("bleujin").setPage(Page.create(3, 1))) ;
		
		List<MyDocument> docs = result.getDocument() ;
		assertEquals(3, docs.size()) ;
	}


	public void testAllDoc() throws Exception {
		MyDocument[] docs = searcher.allDocs();
		assertEquals(24, docs.length) ;
	}
	
	
	public ISearcher makeSearcher(Directory dir) throws IOException {
		return Central.createOrGet(dir).newSearcher() ;
	}

	
}

