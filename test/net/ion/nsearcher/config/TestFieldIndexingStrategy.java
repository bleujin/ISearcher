package net.ion.nsearcher.config;

import java.util.List;

import junit.framework.TestCase;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

public class TestFieldIndexingStrategy extends TestCase {

	public void testDefault() throws Exception {
		Central central = CentralConfig.newRam().build();
		
		Indexer indexer = central.newIndexer();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				WriteDocument doc = MyDocument.newDocument("123").unknown("created", "20010101-121212") ;
				session.updateDocument(doc) ;
				return null;
			}
		}) ;
		
		Searcher searcher = central.newSearcher();
		searcher.createRequest("").find().debugPrint() ;
	}
}
