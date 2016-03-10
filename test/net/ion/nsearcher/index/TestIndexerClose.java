package net.ion.nsearcher.index;

import java.util.concurrent.Executors;

import net.ion.framework.util.ObjectId;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import junit.framework.TestCase;

public class TestIndexerClose extends TestCase {

	
	public void testWhenClosed() throws Exception {
		Central central = CentralConfig.newLocalFile().dirFile("./resource/tem").indexConfigBuilder().executorService(Executors.newCachedThreadPool()).build();
		
		Indexer indexer = central.newIndexer() ;

		for (int i = 0; i < 100 ; i++) {
			indexer.asyncIndex(new IndexJob<Void>() {
				@Override
				public Void handle(IndexSession isession) throws Exception {
					for (int j = 0; j < 3; j++) {
						isession.newDocument(new ObjectId().toString()).keyword("key", "key").text("name", "bleujin").number("age", 10).insert() ;
					}
					return null;
				}
			}) ;
		}
		
		central.close();
	}
}
