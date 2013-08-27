package net.ion.nsearcher.impl;

import junit.framework.TestCase;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

public class TestMultiCentral extends TestCase{

	public void testCreate() throws Exception {
		Central mc = CentralConfig.newRam().build() ;

		Indexer iw = mc.newIndexer() ;
		iw.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					WriteDocument doc = isession.newDocument();
					doc.add(MyField.number("index", i)) ;
					doc.add(MyField.keyword("name", "bleujin")) ;
					isession.insertDocument(doc) ;
				}
				return null;
			}
		}) ;

		
		Searcher searcher = mc.newSearcher() ;
		assertEquals(10, searcher.search("bleujin").size()) ;
		
//		mc.forceCopy() ;
//		
//		ISearcher destSearchr = mc.destSearcher() ;
//		ISearchRequest req = SearchRequest.create("bleujin", "", new StandardAnalyzer(Version.LUCENE_36)) ;
//		assertEquals(10, searcher.search(req).getTotalCount()) ;
		
	}
	
	
	
}
