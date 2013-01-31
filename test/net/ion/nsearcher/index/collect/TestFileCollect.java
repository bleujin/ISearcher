package net.ion.nsearcher.index.collect;

import java.io.File;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.index.NonBlockingListener;

public class TestFileCollect extends ISTestCase{

	public void testNotCollect() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		Indexer indexer = cen.newIndexer();
		
		FileCollector col = new FileCollector(new File("resource/sample"), true);
		NonBlockingListener adapterListener = getNonBlockingListener(indexer);
		col.addListener(adapterListener) ;
		// col.addListener(new DefaultReportor()) ;
		
//		col.collect() ;
		adapterListener.waitForCompleted() ;
		assertEquals(0, cen.newSearcher().searchTest("").getTotalCount()) ;
	}
	
	public void testExecute() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		Indexer indexer = cen.newIndexer();
		
		FileCollector col = new FileCollector(new File("resource/sample"), true);
		NonBlockingListener adapterListener = getNonBlockingListener(indexer);
		col.addListener(adapterListener) ;
		// col.addListener(new DefaultReportor()) ;
		
		col.collect() ;
		adapterListener.waitForCompleted() ;

		assertEquals(3, cen.newSearcher().searchTest("").getTotalCount()) ;
	}

}
