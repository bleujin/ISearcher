package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

public class TestFieldType extends TestCase {

	
	public void testNumericSort() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
	
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.deleteAll() ;
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).text("explain", "hello bleujin").update() ;
				isession.newDocument("hero").keyword("name", "hero").number("age", 30).text("explain", "hi hero").update() ;
				isession.newDocument("jin").keyword("name", "jin").number("age", 7).text("explain", "namaste jin").update() ;
				return null;
			}
		}) ;
		
		Searcher searcher = cen.newSearcher() ;
		
		searcher.createRequest("").ascendingNum("age").find().debugPrint(); 
		
		searcher.createRequest("").sort("age").find().debugPrint(); 
	}
}
