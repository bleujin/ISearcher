package net.ion.nsearcher.search;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.filter.TermFilter;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.TermRangeFilter;

public class TestSearchFilter extends ISTestCase{


	private Central cen = null ; 
	public void setUp() throws Exception {
		super.setUp() ;
		cen = writeDocument() ;
	}
	
	public void testSearchFilter() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		Filter filter = new TermFilter("long", "1234") ;
		searcher.andFilter(filter) ;
		
		
	}
	
	public void testEqual() throws Exception {
		TermFilter filter1 = new TermFilter("long", "1234") ;
		TermFilter filter2 = new TermFilter("long", "1234") ;
		
		assertEquals(true, filter1.equals(filter2)) ;
	}
	
	
	public void testTermFilterAnd() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		TermFilter filter = new TermFilter("long", "1234") ;
		
		searcher.andFilter(filter) ;
		assertEquals(1, searcher.search("test").size()) ;
	}
	
	public void testRequestTermFilterAnd() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		TermFilter filter = new TermFilter("long", "1234") ;
		searcher.andFilter(filter) ;
		
		
		assertEquals(1, searcher.createRequest("test").ascending("long") .find().size()) ;
	}
	
	public void testLongFilterCache() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		//Filter filter = TermRangeFilter.("long", 256, 0L, 1000L, true, true) ;
		
		searcher.andFilter(NumericRangeFilter.newLongRange("long", 4, 0L, 10000L, true, true)) ;
		
		assertEquals(1, searcher.createRequest("test").ascending("long").find().size()) ;
	}
	
	public void testRequestLongFilterCache() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		NumericRangeFilter filter = NumericRangeFilter.newLongRange("long", 32, 0L, 10000L, true, true) ;
		
		//Filter filter = TermRangeFilter.("long", 256, 0L, 1000L, true, true) ;
		
		// searcher.addFilter(filter) ;
		searcher.andFilter(filter) ;
		
		assertEquals(1, searcher.search("test").size()) ;
	}
	
	// 20100725-232010
	public void testDateFilter() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		Filter filter = new TermFilter("date", "20100725") ;

		searcher.andFilter(filter) ;
		
		searcher.search("name:date").debugPrint() ;
		assertEquals(1, searcher.search("name:date").size()) ;
		
		
		// case 2
		searcher = cen.newSearcher() ;
		filter = new TermFilter("date", "20100725") ;

		searcher.andFilter(filter) ;
		assertEquals(1, searcher.search("name:date").size()) ;
		
		// case 3
		searcher = cen.newSearcher() ;
		filter = NumericRangeFilter.newLongRange("date", 32, 20100625L, 20100725L, true, true) ;
		searcher.andFilter(filter) ;
		assertEquals(1, searcher.search("name:date").size()) ;

		// case 4
		searcher = cen.newSearcher() ;
		filter = NumericRangeFilter.newLongRange("date", 32, 20100625L, 20100724L, true, true) ;
		searcher.andFilter(filter) ;
		assertEquals(0, searcher.search("name:date").size()) ;

//		searcher = cen.newSearcher() ;
//		assertEquals(true, 20100700000000L < 20100725232010L) ;
//		assertEquals(true, 20100725232010L < 20100801235959L) ;
//		Filter filter1 = NumericRangeFilter.newLongRange("datetime", 32, 20100601235959L, 20110801235959L, true, true) ;
//		ISearchRequest srequest1 = SearchRequest.test("name:date") ;
//		searcher.andFilter(filter1) ;
//		assertEquals(1, searcher.search(srequest1).getTotalCount()) ;
	
		// case 5
//		searcher = cen.newSearcher() ;
//		Filter filter1 = NumericRangeFilter.newLongRange("datetime", 32, 201001000000L, 201007230000L, true, true) ;
//		ISearchRequest srequest1 = SearchRequest.test("name:date") ;
//		searcher.andFilter(filter1) ;
//		assertEquals(0, searcher.search(srequest1).getTotalCount()) ;
	}

	public void testDateRange() throws Exception {
		// cast6
		Searcher searcher = cen.newSearcher();
		Filter filter1 =  new TermRangeFilter("date", "20100725", "20100725-2327", true, true); // myDoc4.add(MyField.date("date", 20100725, 232010)) ;
		searcher.andFilter(filter1) ;
		assertEquals(1, searcher.search("name:date").totalCount()) ;

	}

	

}
