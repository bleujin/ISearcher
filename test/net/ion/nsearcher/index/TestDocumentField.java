package net.ion.nsearcher.index;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import junit.framework.TestCase;

public class TestDocumentField extends TestCase{

	
	public void testFromJson() throws Exception {
		final JsonObject json = JsonObject.fromString("{name:'bleujin', age:20}") ;
		
		Central cen = CentralConfig.newRam().build();
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("json").add(json).update() ;
				return null;
			}
		}) ;
		
		
		ReadDocument doc = cen.newSearcher().search("name:bleujin").first() ;
		
		assertEquals(true, ArrayUtil.contains(doc.fieldNames(), "name"));
		assertEquals(true, ArrayUtil.contains(doc.fieldNames(), "age"));
	}
	
}
