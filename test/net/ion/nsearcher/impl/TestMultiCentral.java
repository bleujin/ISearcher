package net.ion.nsearcher.impl;

import junit.framework.TestCase;
import net.ion.nsearcher.Searcher;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class TestMultiCentral extends TestCase{

	public void testCreate() throws Exception {
		Central mc = CentralConfig.newRam().build() ;

		Indexer iw = mc.newIndexer() ;
		iw.index(new MyKoreanAnalyzer(), new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				for (int i = 0; i < 10; i++) {
					MyDocument doc = MyDocument.testDocument();
					doc.add(MyField.number("index", i)) ;
					doc.add(MyField.keyword("name", "bleujin")) ;
					session.insertDocument(doc) ;
				}
				return null;
			}
		}) ;

		
		Searcher searcher = mc.newSearcher() ;
		SearchRequest req = SearchRequest.create("bleujin", "", new StandardAnalyzer(Version.LUCENE_36)) ;
		assertEquals(10, searcher.search(req).getTotalCount()) ;
		
//		mc.forceCopy() ;
//		
//		ISearcher destSearchr = mc.destSearcher() ;
//		ISearchRequest req = SearchRequest.create("bleujin", "", new StandardAnalyzer(Version.LUCENE_36)) ;
//		assertEquals(10, searcher.search(req).getTotalCount()) ;
		
	}
	
	
	
}
