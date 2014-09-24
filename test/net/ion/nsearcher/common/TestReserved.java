package net.ion.nsearcher.common;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import junit.framework.TestCase;

public class TestReserved extends TestCase{

	public void testId() throws Exception {
		Central cen = CentralConfig.newRam().build();
	 	
		Indexer indexer = cen.newIndexer();
	 	indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				return isession.newDocument().unknown("name", "bleujin").insertVoid() ;
			}
		}) ;
	 	
	 	ReadDocument doc = cen.newSearcher().search("").first() ;
	 	
	 	Debug.line(doc.reserved(IKeywordField.DocKey));

	}
}
