package net.ion.bleujin.lucene;

import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import junit.framework.TestCase;

public class TestHanga extends TestCase {

	public void testHanga() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().keyword("name", "三星電氣").insert() ;
				return null;
			}
		}) ;
		
		cen.newSearcher().createRequest("name:三星*").find().debugPrint();
		
	}
}
