package net.ion.isearcher.impl;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.filter.TermFilter;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;

public class SearchFilterTest extends ISTestCase{


	private Central cen = null ; 
	public void setUp() throws Exception {
		super.setUp() ;
		cen = Central.createOrGet(writeDocument()) ;
	}
	
	public void testSearchFilter() throws Exception {
		ISearcher searcher = cen.newSearcher() ;
		Filter filter = new TermFilter() ;
		searcher.andFilter(filter) ;
		
		assertEquals(true, cen.centralFilter().existFilter(filter)) ;
	}
	
	
	public void testTermFilterAnd() throws Exception {
		ISearcher searcher = cen.newSearcher() ;
		TermFilter filter = new TermFilter() ;
		filter.addTerm(new Term("long", "1234")) ;
		
		searcher.andFilter(filter) ;
		ISearchRequest srequest = SearchRequest.test("test") ;
		assertEquals(1, searcher.search(srequest).getTotalCount()) ;
	}
	
	public void testRequestTermFilterAnd() throws Exception {
		ISearcher searcher = cen.newSearcher() ;
		TermFilter filter = new TermFilter() ;
		filter.addTerm(new Term("long", "1234")) ;
		
		ISearchRequest srequest = SearchRequest.test("test", "long") ;
		searcher.andFilter(filter) ;
		
		
		assertEquals(1, searcher.search(srequest).getTotalCount()) ;
	}
	
	public void testLongFilterCache() throws Exception {
		ISearcher searcher = cen.newSearcher() ;
		NumericRangeFilter filter = NumericRangeFilter.newLongRange("long", 4, 0L, 10000L, true, true) ;
		
		//Filter filter = TermRangeFilter.("long", 256, 0L, 1000L, true, true) ;
		
		searcher.andFilter(filter) ;
		ISearchRequest srequest = SearchRequest.test("test", "long") ;
		
		assertEquals(1, searcher.search(srequest).getTotalCount()) ;
	}
	
	public void testRequestLongFilterCache() throws Exception {
		ISearcher searcher = cen.newSearcher() ;
		NumericRangeFilter filter = NumericRangeFilter.newLongRange("long", 32, 0L, 10000L, true, true) ;
		
		//Filter filter = TermRangeFilter.("long", 256, 0L, 1000L, true, true) ;
		
		// searcher.addFilter(filter) ;
		ISearchRequest srequest = SearchRequest.test("test") ;
		searcher.andFilter(filter) ;
		
		assertEquals(1, searcher.search(srequest).getTotalCount()) ;
	}
	

	// 20100725-232010
	public void testDateFilter() throws Exception {
		ISearcher searcher = cen.newSearcher() ;
		Filter filter = new TermFilter() ;
		((TermFilter)filter).addTerm(new Term("date", "20100725")) ;

		searcher.andFilter(filter) ;
		ISearchRequest srequest = SearchRequest.test("name:date") ;
		assertEquals(1, searcher.search(srequest).getTotalCount()) ;
		
		
		// case 2
		searcher = cen.newSearcher() ;
		filter = new TermFilter() ;
		((TermFilter)filter).addTerm(new Term("date", "20100725")) ;

		srequest = SearchRequest.test("name:date") ;
		searcher.andFilter(filter) ;
		assertEquals(1, searcher.search(srequest).getTotalCount()) ;
		
		// case 3
		searcher = cen.newSearcher() ;
		filter = NumericRangeFilter.newLongRange("date", 32, 20100625L, 20100725L, true, true) ;
		srequest = SearchRequest.test("name:date") ;
		searcher.andFilter(filter) ;
		assertEquals(1, searcher.search(srequest).getTotalCount()) ;

		// case 4
		searcher = cen.newSearcher() ;
		filter = NumericRangeFilter.newLongRange("date", 32, 20100625L, 20100724L, true, true) ;
		srequest = SearchRequest.test("name:date") ;
		searcher.andFilter(filter) ;
		assertEquals(0, searcher.search(srequest).getTotalCount()) ;

		searcher = cen.newSearcher() ;
		assertEquals(true, 20100700000000L < 20100725232010L) ;
		assertEquals(true, 20100725232010L < 20100801235959L) ;
		Filter filter1 = NumericRangeFilter.newLongRange("datetime", 32, 20100601235959L, 20110801235959L, true, true) ;
		ISearchRequest srequest1 = SearchRequest.test("name:date") ;
		searcher.andFilter(filter1) ;
		assertEquals(1, searcher.search(srequest1).getTotalCount()) ;
	
		// case 5
		searcher = cen.newSearcher() ;
		filter1 = NumericRangeFilter.newLongRange("datetime", 32, 201001000000L, 201007230000L, true, true) ;
		srequest1 = SearchRequest.test("name:date") ;
		searcher.andFilter(filter1) ;
		assertEquals(0, searcher.search(srequest1).getTotalCount()) ;
		
	}
	

	

}
