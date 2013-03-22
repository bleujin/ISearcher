package net.ion.nsearcher.impl;

import java.io.IOException;
import java.util.Date;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.search.NumericRangeFilter;

public class TestField extends ISTestCase{

	public void testUnknownNumber() throws Exception {
		Central cen = writeDocument() ;
		
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws IOException {
				MyDocument doc = MyDocument.testDocument() ;
				doc.add(MyField.keyword("name", "test"));
				doc.add(MyField.unknown("intkey", 123));
				session.updateDocument(doc) ;
				return null ;
			}
		}) ;

		Searcher searcher = cen.newSearcher() ;
		searcher.andFilter(NumericRangeFilter.newLongRange("intkey", 8, 0L, 10000L, true, true)) ;
		assertEquals(1, searcher.createRequest("test").find().size()) ;
	}

	public void testUnknownDate() throws Exception {
		Central cen = writeDocument() ;
		
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				MyDocument doc = MyDocument.testDocument() ;
				doc.add(MyField.keyword("name", "test"));
				doc.add(MyField.unknown("datekey", new Date()));
				
				session.updateDocument(doc) ;
				return null;
			}
		}) ;

		Searcher searcher = cen.newSearcher() ;
//		searcher.andFilter(NumericRangeFilter.newLongRange("datekey", 8, 20100101L, 20111231L, true, true)) ;
		assertEquals(2, searcher.search("test").size()) ;
		
		
		// "date", 20100725, 232010))
		searcher.search("date:[\"20100725 16\" TO \"20100726 17\"]").debugPrint() ;
		//searcher.searchTest("date:\"20110530 164134\"").debugPrint(Page.ALL) ;
		
//		Date d = DateFormatUtil.getDateIfMatchedType("20110530-164134");
//		Calendar c = DateUtil.dateToCalendar(d);
//		int yyyymmdd = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DATE);
//		int hh24miss = c.get(Calendar.HOUR) * 10000 + c.get(Calendar.MINUTE) * 100 + c.get(Calendar.SECOND);
//		Debug.line(hh24miss);
	}

}
