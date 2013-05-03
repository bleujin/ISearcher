package net.ion.nsearcher.central;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.processor.StdOutProcessor;

public class TestCentral extends ISTestCase{

	public void testMakeSearcher() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		central.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.insertDocument(WriteDocument.newDocument("bleujin")) ;
				return null;
			}
		}) ;
		
		
		Searcher searcher = central.newSearcher() ;
		searcher.addPostListener(new StdOutProcessor()) ;
		assertEquals(true, searcher.search("bleujin").getDocument().size() > 0) ; 
	}
	

}

