package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

public class TestFieldType extends TestCase {

	
	private Central cen;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.cen = CentralConfig.newRam().build() ;
		
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.deleteAll() ;
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20L).stext("explain", "hello bleujin").update() ;
//				isession.newDocument("hero").keyword("name", "hero").number("age", 30).text("explain", "hi hero").update() ;
//				isession.newDocument("jin").keyword("name", "jin").number("age", 7).text("explain", "namaste jin").update() ;
				return null;
			}
		}) ;
	}
	
	public void testNumericSort() throws Exception {
		
		Searcher searcher = cen.newSearcher() ;
		
		searcher.createRequest("").ascendingNum("age").find().debugPrint(); 
		searcher.createRequest("").sort("age").find().debugPrint(); 
	}
	
	public void testCaseSensitive() throws Exception {
		assertEquals(1, cen.newSearcher().createRequestByTerm("explain", "bleujin").find().size()) ;
		assertEquals(0, cen.newSearcher().createRequestByTerm("Explain", "bleujin").find().size()) ;
	}
	
	public void testLongSaved() throws Exception {
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument wdoc = isession.loadDocument("bleujin", true, "age").keyword("nfield", "new").update() ;
				return null;
			}
		}) ;
		cen.newSearcher().createRequestByTerm("age", "" + 20).find().debugPrint();
		cen.newSearcher().createRequest("age:[20 TO 20]").find().debugPrint();
		cen.newSearcher().createRequest("hello").find().debugPrint();

		assertEquals(1, cen.newSearcher().createRequestByTerm("age", "" + 20).find().size()) ;
		assertEquals(1, cen.newSearcher().createRequest("age:[20 TO 20]").find().size()) ;
		assertEquals(1, cen.newSearcher().createRequest("hello").find().size()) ;

		
		//		cen.newSearcher().createRequestByTerm("explain", "bleujin").find().debugPrint();
	}
}
