package net.ion.nsearcher.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import junit.framework.TestCase;

public class TestAnalWrapper extends TestCase{

	public void testIndex() throws Exception {
		Central c = CentralConfig.newRam().build() ;
		
		Version v = Version.LUCENE_CURRENT ;
		PerFieldAnalyzerWrapper ianal = new PerFieldAnalyzerWrapper(new CJKAnalyzer(v), MapUtil.<String, Analyzer>create("name", new KeywordAnalyzer())) ;
		
		c.newIndexer().index(ianal, new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("123").unknown("name", "태극기").insert() ;
				return null;
			}
		}) ;
		
		Searcher searcher = c.newSearcher() ;
		assertEquals(1, searcher.search("").size()) ; 
		
		assertEquals(1, searcher.createRequest("name:태극기", new KeywordAnalyzer()).find().size()) ;
		assertEquals(0, searcher.createRequest("name:태극기", new CJKAnalyzer(v)).find().size()) ;
		assertEquals(1, searcher.createRequest("태극기", new CJKAnalyzer(v)).find().size()) ;
		assertEquals(0, searcher.createRequest("태극기", new KeywordAnalyzer()).find().size()) ;
		
		

		assertEquals(1, searcher.createRequest("name:태극기", ianal).find().size()) ;
		assertEquals(1, searcher.createRequest("태극기", ianal).find().size()) ;

		c.close(); 
	}
	
	public void testQuery() throws Exception {
		
	}
}
