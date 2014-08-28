package net.ion.nsearcher.central;

import junit.framework.TestCase;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.processor.StdOutProcessor;

public class TestCentral extends TestCase{

	public void testMakeSearcher() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		central.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("newdoc").insert();
				return null;
			}
		}) ;
		
		
		Searcher searcher = central.newSearcher() ;
		searcher.addPostListener(new StdOutProcessor()) ;
		assertEquals(true, searcher.search("newdoc").getDocument().size() > 0) ;
		
		central.close(); 
	}
	

}

