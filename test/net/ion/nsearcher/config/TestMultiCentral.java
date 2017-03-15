package net.ion.nsearcher.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.ion.framework.util.InfinityThread;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import junit.framework.TestCase;

public class TestMultiCentral extends TestCase {

	
	public void testFirst() throws Exception {
		Central c1 = CentralConfig.newLocalFile().dirFile("./resource/aindex1")
					.indexConfigBuilder().executorService(Executors.newSingleThreadExecutor()).parent().searchConfigBuilder().executorService(Executors.newCachedThreadPool()).build();
		Central c2 = CentralConfig.newLocalFile().dirFile("./resource/aindex2")
					.indexConfigBuilder().executorService(Executors.newSingleThreadExecutor()).parent().searchConfigBuilder().executorService(Executors.newCachedThreadPool()).build();
		
		c1.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					isession.newDocument(i + "").keyword("index", "" + i).number("num", i).updateVoid() ;
				}
				return null;
			}
		}) ;
		
		c2.newIndexer().asyncIndex(new IndexJob<Void>() {

			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					isession.newDocument(i + "").keyword("index", "" + i).number("num", i).updateVoid() ;
					Thread.sleep(1000 * 10);
				}
				return null;
			}
		}) ;
		
		
		c1.newSearcher().createRequest("").find().debugPrint(); 
		Thread.sleep(100);
		
		
		c2.newSearcher().createRequest("").find().debugPrint("index");
		new InfinityThread().startNJoin(); 
		
		
	}
	
}
