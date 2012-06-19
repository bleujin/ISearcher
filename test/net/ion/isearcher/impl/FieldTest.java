package net.ion.isearcher.impl;

import java.util.Date;

import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.store.Directory;

import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.SearchRequest;

public class FieldTest extends ISTestCase{

	public void testUnknownNumber() throws Exception {
		Directory dir = writeDocument() ;
		Central cen = Central.createOrGet(dir) ;
		
		IWriter writer = cen.newIndexer(new KoreanAnalyzer()) ;
		writer.begin("test") ;
		MyDocument doc = MyDocument.testDocument() ;
		doc.add(MyField.keyword("name", "test"));
		doc.add(MyField.unknown("intkey", 123));
		
		writer.updateDocument(doc) ;
		writer.end() ;

		ISearcher searcher = cen.newSearcher() ;
		NumericRangeFilter filter = NumericRangeFilter.newLongRange("intkey", 32, 0L, 10000L, true, true) ; 
		searcher.andFilter(filter) ;
		ISearchRequest srequest = SearchRequest.test("test") ;
		assertEquals(1, searcher.search(srequest).getTotalCount()) ;
	}

	public void testUnknownDate() throws Exception {
		Directory dir = writeDocument() ;
		Central cen = Central.createOrGet(dir) ;
		
		IWriter writer = cen.newIndexer(new KoreanAnalyzer()) ;
		writer.begin("test") ;
		MyDocument doc = MyDocument.testDocument() ;
		doc.add(MyField.keyword("name", "test"));
		doc.add(MyField.unknown("datekey", new Date()));
		
		writer.updateDocument(doc) ;
		writer.end() ;

		ISearcher searcher = cen.newSearcher() ;
		NumericRangeFilter filter = NumericRangeFilter.newLongRange("datekey", 32, 20100101L, 20111231L, true, true) ; 
		// searcher.andFilter(filter) ;
		ISearchRequest srequest = SearchRequest.test("test") ;
		assertEquals(2, searcher.search(srequest).getTotalCount()) ;
	}

}
