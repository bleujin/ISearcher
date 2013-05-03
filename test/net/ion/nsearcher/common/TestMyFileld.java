package net.ion.nsearcher.common;

import java.util.List;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

public class TestMyFileld extends ISTestCase{

	public void testUnknown() throws Exception {
		WriteDocument doc = WriteDocument.testDocument() ;
		doc.add(MyField.unknown("double", 10.0d)) ;
		doc.add(MyField.unknown("float", 10.0f)) ;

		for(MyField f : doc.getFields()){
			Debug.line(f) ;
		} 
	}
	
	public void testMap() throws Exception {
		WriteDocument doc = createSampleDoc();

		for(MyField f : doc.getFields()){
			Debug.line(f) ;
		} 
	}

	private WriteDocument createSampleDoc() {
		Map<String, Object> address = MapUtil.chainKeyMap().put("city", "seoul").put("bun", 20).toMap() ;
		List<String> names = ListUtil.toList("jin", "hero") ;
		Map<String, Object> values = MapUtil.chainKeyMap().put("name", "bleujin").put("address", address).put("names", names) .toMap() ;
		WriteDocument doc = WriteDocument.newDocument("111").add(values) ;
		return doc;
	}
	
	public void testSearch() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		Indexer writer = cen.newIndexer() ;
		writer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.updateDocument(createSampleDoc()) ;
				return null ;
			}
		}) ;
		
		Searcher searcher = cen.newSearcher() ;
		assertEquals(1, searcher.search("names:jin").size()) ;
		assertEquals(1, searcher.search("names:hero").size()) ;
		
		assertEquals(1, searcher.search("address:20").size()) ;
		assertEquals(1, searcher.search("address.bun:20").size()) ;
		assertEquals(1, searcher.search("address.city:seoul").size()) ;
		assertEquals(1, searcher.search("20").size()) ;
	}
}
