package net.ion.nsearcher.index;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.Searcher;

public class TestIndexSchema extends TestCase{

	public void testLoadIndexSchema() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		Indexer indexer = central.newIndexer() ;
		Searcher searcher = central.newSearcher() ;
		indexer.index(new IndexJob<Void>() {

			@Override
			public Void handle(IndexSession isession) throws Exception {
				// .indexSchemaBuilder().keyword("name").number("age").text("explain")
				isession.fieldIndexingStrategy() ;
				
				WriteDocument wdoc = isession.newDocument("bleujin").add(new JsonObject().put("name", "bleujin").put("age", 20).put("explain", "bleujin hi")) ;
				isession.updateDocument(wdoc) ;
				return null;
			}
		}) ;
		
		ReadDocument rdoc = searcher.createRequest("bleujin").findOne() ;
		Debug.line(rdoc.getFieldNames()) ;
		Debug.line(rdoc.getField("name"), rdoc.getField("explain")) ;
	}
	
}
