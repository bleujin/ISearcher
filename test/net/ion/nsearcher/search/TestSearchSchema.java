package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class TestSearchSchema extends TestCase{

	public void testAnalyzerPerField() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		assertEquals(StandardAnalyzer.class, central.indexConfig().indexAnalyzer().getClass()) ;

		central.searchConfig().fieldAnalyzer("eng", new KeywordAnalyzer()).fieldAnalyzer("stan", new StandardAnalyzer(SearchConstant.LuceneVersion)) ;
		assertEquals(PerFieldAnalyzerWrapper.class, central.searchConfig().queryAnalyzer().getClass()) ;
		
		central.destroySelf(); 
	}

	
	public void testSearch() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		central.searchConfig().fieldAnalyzer("eng", new KeywordAnalyzer()).fieldAnalyzer("cjk", new CJKAnalyzer(SearchConstant.LuceneVersion)).fieldAnalyzer("stan", new StandardAnalyzer(SearchConstant.LuceneVersion)) ;

		
		final JsonObject json = new JsonObject().put("eng", "bleujin").put("cjk", "태극기가 바람에").put("stan", "태극기가") ;
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				return isession.newDocument("bleujin").add(json).insertVoid() ;
			}
		}) ;
		
		
		Searcher searcher = central.newSearcher() ;
		assertEquals(1, searcher.createRequest("eng:bleujin").find().size()) ;
		
		
		assertEquals(0, searcher.createRequest("태극").find().size()) ; // in body builder
		assertEquals(0, searcher.createRequest("cjk:태극기가").find().size()) ; // used cjk

		
		assertEquals(0, searcher.createRequest("stan:태극").find().size()) ; // used cjk
		assertEquals(1, searcher.createRequest("stan:태극기가").find().size()) ; // used cjk
	}

}
