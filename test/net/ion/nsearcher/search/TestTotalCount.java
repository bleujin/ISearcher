package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

public class TestTotalCount extends TestCase {

	public void testAfterReloadingIndex() throws Exception {
		Central central = CentralConfig.newRam().build();
		
		Indexer indexer = central.newIndexer();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.insertDocument(WriteDocument.testDocument().keyword("name", "bleujin")) ;
				return null;
			}
		}) ;
		
		Searcher searcher = central.newSearcher();
		final SearchResponse response = searcher.search("");
		assertEquals(1, response.totalCount()) ;

		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.insertDocument(WriteDocument.testDocument().keyword("name", "hero")) ;
				return null;
			}
		}) ;

		assertEquals(1, response.totalCount()) ;
	}
}
