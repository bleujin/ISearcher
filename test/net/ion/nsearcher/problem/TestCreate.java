package net.ion.nsearcher.problem;

import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import junit.framework.TestCase;

public class TestCreate extends TestCase{

	
	public void testInsertSameKey() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("mykey").keyword("name", "bleujin").update() ;
				isession.newDocument("mykey").keyword("name", "bleujin").insert() ;
				return null;
			}
		}) ;
		
		central.newSearcher().createRequest("").find().debugPrint(); 
	}
}
