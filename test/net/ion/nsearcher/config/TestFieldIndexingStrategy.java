package net.ion.nsearcher.config;

import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;
import net.ion.framework.util.DateFormatUtil;
import net.ion.framework.util.DateUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.IndexField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

import org.apache.commons.validator.GenericValidator;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class TestFieldIndexingStrategy extends TestCase {

	public void testDefault() throws Exception {
		Central central = CentralConfig.newRam().build();
		
		Indexer indexer = central.newIndexer();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				WriteDocument doc = WriteDocument.newDocument("123").unknown("num", "50") ; // maybe numeric
				session.updateDocument(doc) ;
				return null;
			}
		}) ;
		
		Searcher searcher = central.newSearcher();
		assertEquals(1, searcher.createRequest("num:[+40 TO +100]").find().size()) ;
		assertEquals(1, searcher.createRequest("num:50").find().size()) ;
	}
	
	public void testMyStrategy() throws Exception {
		Central central = CentralConfig.newRam().indexConfigBuilder().setFieldIndexingStrategy(new TestStrategy()).build();
		
		Indexer indexer = central.newIndexer();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				WriteDocument doc = WriteDocument.newDocument("123").unknown("num", "50") ; // keyword 
				session.updateDocument(doc) ;
				return null;
			}
		}) ;
		
		Searcher searcher = central.newSearcher();
		assertEquals(0, searcher.createRequest("num:[+40 TO +100]").find().size()) ;
		assertEquals(1, searcher.createRequest("num:50").find().size()) ;
	}
	
	
	public void testDateCheckSpeed() throws Exception {
		Object lastResult = null ; 
		for (int i = 0; i < 20000; i++) {
//			Date d = DateFormatUtil.getDateIfMatchedType("2001.11.11");
			lastResult = Character.isDigit("2001.11.11".charAt(0)) && Character.isDigit("2001.11.11".charAt(1));
//			lastResult = GenericValidator.isDate("2001.11.11", "yyyy/mm/dd", false) ;
//			System.out.println(d != null) ;
		}
		System.out.print(lastResult) ;
	}
	
	
	
}

class TestStrategy extends FieldIndexingStrategy {
	
	public IndexField unknown(String name, String value) {
		return FieldIndexingStrategy.DEFAULT.keyword(name, value) ;
	}
	

	public IndexField keyword(String name, String value) {
		return FieldIndexingStrategy.DEFAULT.keyword(name, value) ;
	}

	public IndexField number(String name, long number) {
		return FieldIndexingStrategy.DEFAULT.number(name, number) ;
	}

	public IndexField number(String name, double number) {
		return FieldIndexingStrategy.DEFAULT.number(name, number) ;
	}

	public IndexField date(String name, int yyyymmdd, int hh24miss) {
		return FieldIndexingStrategy.DEFAULT.date(name, yyyymmdd, hh24miss) ;
	}
	
	public IndexField text(String name, String value) {
		return FieldIndexingStrategy.DEFAULT.text(name, value) ;
	}
	
	
	public IndexField noStoreText(String name, String value) {
		return FieldIndexingStrategy.DEFAULT.noStoreText(name, value) ;
	}
	
	public IndexField unknown(String name, Object obj){
		return FieldIndexingStrategy.DEFAULT.unknown(name, obj) ;

	}
	public IndexField manual(String name, String value, Store store, Index index) {
		return FieldIndexingStrategy.DEFAULT.manual(name, value, store, index) ;
	}

};