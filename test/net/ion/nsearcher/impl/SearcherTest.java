package net.ion.nsearcher.impl;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.Searcher;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.processor.StdOutProcessor;

public class SearcherTest extends ISTestCase {

	private Searcher searcher;
	private Central central = null ; 
	public void setUp() throws Exception {
		central = writeDocument() ;
		searcher = central.newSearcher() ;
	}

	public void tearDown() throws Exception {
		central.destroySelf() ;
	}


	public void testSearchCount() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search(SearchRequest.create("bleujin"));
		List<MyDocument> docs = result.getDocument();
		assertEquals(6, result.getTotalCount());
	}
	

	public void testSearchFieldCount() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search(SearchRequest.create("mysub:(bleujin novision) OR subject:(bleujin novision)"));
		List<MyDocument> docs = result.getDocument();
//		for (MyDocument doc : docs) {
//			Debug.line(doc) ;
//		}
	}

	public void testPage() throws Exception {
		SearchResponse result = searcher.search(SearchRequest.create("bleujin").offset(3)) ;
		
		List<MyDocument> docs = result.getDocument() ;
		assertEquals(3, docs.size()) ;
	}


	public void testAllDoc() throws Exception {
		List<MyDocument> docs = searcher.search(SearchRequest.create("")).getDocument();
		assertEquals(24, docs.size()) ;
	}
}

