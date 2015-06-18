package net.ion.nsearcher.config;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

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
		assertEquals(1, searcher.createRequest("num:50").find().size()) ;
		
		Debug.line(searcher.createRequest("num:[+40 TO +100]").query());
		
		assertEquals(1, searcher.createRequest("num:[+40 TO +100]").find().size()) ;
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
		assertEquals(1, searcher.createRequest("num:[+40 TO +100]").find().size()) ;
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
		Debug.debug(lastResult) ;
	}
	
	
	
}

class TestStrategy extends FieldIndexingStrategy {

	@Override
	public void save(Document doc, MyField myField, Field ifield) {
		FieldIndexingStrategy.DEFAULT.save(doc, myField, ifield);
	}


};