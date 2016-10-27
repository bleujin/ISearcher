package net.ion.nsearcher.index.fileindex;

import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import junit.framework.TestCase;

public class TestRollback extends TestCase {

	
	public void testRun() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		Indexer indexer = central.newIndexer() ;
		
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				return isession.newDocument().keyword("id", "bleujin").updateVoid() ;
			}
		}) ;
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				throw new IllegalArgumentException("not index") ;
			}
		}) ;
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				return isession.newDocument().keyword("id", "hero").updateVoid() ;
			}
		}) ;
		
		
		central.newSearcher().search("").debugPrint(); 
	}
}
