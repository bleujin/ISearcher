package net.ion.nsearcher.central;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;


public class TestIndexWriteConfig extends ISTestCase{

	
	public void testSetGet() throws Exception {
		CentralConfig config = CentralConfig.newRam().indexConfigBuilder().setMaxBufferedDocs(100).parent();
		Central cen = config.build() ;
		cen.newIndexer().index(createDefaultAnalyzer(), new IndexJob<Void>() {

			public Void handle(IndexSession session) throws Exception {
				assertEquals(100, session.getIndexWriterConfig().getMaxBufferedDocs()) ;
				return null;
			}
		}) ;
		
		
	}
}
