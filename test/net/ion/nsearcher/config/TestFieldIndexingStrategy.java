package net.ion.nsearcher.config;

import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;
import net.ion.framework.util.DateFormatUtil;
import net.ion.framework.util.DateUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.IndexField;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

import org.apache.commons.validator.GenericValidator;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class TestFieldIndexingStrategy extends TestCase {

	public void testDefault() throws Exception {
		Central central = CentralConfig.newRam().build();
		
		Indexer indexer = central.newIndexer();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument("123").unknown("num", "50") ; // maybe numeric
				isession.updateDocument(doc) ;
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
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument("123").unknown("num", "50") ; // keyword 
				isession.updateDocument(doc) ;
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

	@Override
	public void date(Document doc, MyField field, String name, int yyyymmdd, int hh24miss) {
		FieldIndexingStrategy.DEFAULT.date(doc, field, name, yyyymmdd, hh24miss) ;
	}

	@Override
	public void keyword(Document doc, MyField field, String name, String value) {
		FieldIndexingStrategy.DEFAULT.keyword(doc, field, name, value) ;
	}

	@Override
	public void manual(Document doc, String name, String value, Store store, Index index) {
		FieldIndexingStrategy.DEFAULT.manual(doc, name, value, store, index) ;
	}

	@Override
	public void noStoreText(Document doc, MyField field, String name, String value) {
		FieldIndexingStrategy.DEFAULT.noStoreText(doc, field, name, value) ;
	}

	@Override
	public void number(Document doc, MyField field, String name, long number) {
		FieldIndexingStrategy.DEFAULT.number(doc, field, name, number) ;
	}

	@Override
	public void number(Document doc, MyField field, String name, double number) {
		FieldIndexingStrategy.DEFAULT.number(doc, field, name, number) ;
	}

	@Override
	public void text(Document doc, MyField field, String name, String value) {
		FieldIndexingStrategy.DEFAULT.text(doc, field, name, value) ;
	}

	@Override
	public void unknown(Document doc, MyField field, String name, Object obj) {
		FieldIndexingStrategy.DEFAULT.unknown(doc, field, name, obj) ;
	}

	@Override
	public void unknown(Document doc, MyField field, String name, String value) {
		FieldIndexingStrategy.DEFAULT.unknown(doc, field, name, value) ;
	}
	

};