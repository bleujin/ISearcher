package net.ion.isearcher.common;

import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Fieldable;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;
import junit.framework.TestCase;

public class TestMyFileld extends TestCase{

	public void testUnknown() throws Exception {
		MyDocument doc = MyDocument.testDocument() ;
		doc.add(MyField.unknown("double", 10.0d)) ;
		doc.add(MyField.unknown("float", 10.0f)) ;

		for(Fieldable f : doc.getFields()){
			Debug.line(f) ;
		} 
		
	}
	
	public void testMap() throws Exception {
		MyDocument doc = createSampleDoc();

		for(Fieldable f : doc.getFields()){
			Debug.line(f) ;
		} 
	}

	private MyDocument createSampleDoc() {
		Map<String, Object> address = MapUtil.chainKeyMap().put("city", "seoul").put("bun", 20).toMap() ;
		List<String> names = ListUtil.toList("jin", "hero") ;
		Map<String, Object> values = MapUtil.chainKeyMap().put("name", "bleujin").put("address", address).put("names", names) .toMap() ;
		MyDocument doc = MyDocument.newDocument("111", values) ;
		return doc;
	}
	
	public void testSearch() throws Exception {
		Central cen = Central.createOrGet(new RAMDirectory()) ;
		IWriter writer = cen.newIndexer(new MyKoreanAnalyzer()) ;
		writer.begin("test") ;
		writer.updateDocument(createSampleDoc()) ;
		writer.end() ;
		
		ISearcher searcher = cen.newSearcher() ;
		assertEquals(1, searcher.searchTest("names:jin").getTotalCount()) ;
		assertEquals(1, searcher.searchTest("names:hero").getTotalCount()) ;
		
		assertEquals(1, searcher.searchTest("address:20").getTotalCount()) ;
		assertEquals(1, searcher.searchTest("address.bun:20").getTotalCount()) ;
		assertEquals(1, searcher.searchTest("address.city:seoul").getTotalCount()) ;
		assertEquals(1, searcher.searchTest("20").getTotalCount()) ;
	}
}
