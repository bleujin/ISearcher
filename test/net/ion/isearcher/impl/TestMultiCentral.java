package net.ion.isearcher.impl;

import junit.framework.TestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;
import net.ion.isearcher.searcher.SearchRequest;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class TestMultiCentral extends TestCase{

	public void testCreate() throws Exception {
		Directory buffer = new RAMDirectory() ;
		Directory store = new RAMDirectory() ;
		MultiCentral mc = Central.createOrGet(buffer, store) ;

		IWriter iw = mc.newIndexer(new MyKoreanAnalyzer()) ;
		iw.begin("test") ;
		
		for (int i = 0; i < 10; i++) {
			MyDocument doc = MyDocument.testDocument();
			doc.add(MyField.number("index", i)) ;
			doc.add(MyField.keyword("name", "bleujin")) ;
			iw.insertDocument(doc) ;
		}
		iw.end() ;
		
		ISearcher searcher = mc.newSearcher() ;
		ISearchRequest req = SearchRequest.create("bleujin", "", new StandardAnalyzer(Version.LUCENE_36)) ;
		assertEquals(10, searcher.search(req).getTotalCount()) ;
		
//		mc.forceCopy() ;
//		
//		ISearcher destSearchr = mc.destSearcher() ;
//		ISearchRequest req = SearchRequest.create("bleujin", "", new StandardAnalyzer(Version.LUCENE_36)) ;
//		assertEquals(10, searcher.search(req).getTotalCount()) ;
		
	}
	
	
	
}
