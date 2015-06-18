package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

public class TestFirst extends TestCase{

	
	public void testFirst() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		Indexer indexer = central.newIndexer() ;
		
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 5; i++) {
					isession.newDocument("bleujin" + i).unknown("id", "bleujin" + i).unknown("content", "hello world").unknown("age", 20).update() ;
				}
				return null;
			}
		}) ;
		
		
		Searcher searcher = central.newSearcher() ;
		SearchResponse response = searcher.createRequest("20").descending("id").find() ;
		
		
		response.eachDoc(new EachDocHandler<Void>() {

			@Override
			public <T> T handle(EachDocIterator iter) {
				while(iter.hasNext()){
					Debug.line(iter.next());
				}
				return null;
			}
		}) ;
		
		
		
		
		central.close(); 
	}
}
