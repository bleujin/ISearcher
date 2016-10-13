package net.ion.nsearcher.search;

import java.io.IOException;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

public class FirstTest extends TestCase{

	public void testBlankSearcher() throws Exception {
		Central c = CentralConfig.newRam().build() ;
		Searcher searcher = c.newSearcher() ;
		searcher.search("").debugPrint() ;
	}
	
	public void testSearch() throws Exception {
		final Central cen = CentralConfig.newRam().indexConfigBuilder().indexAnalyzer(new CJKAnalyzer()).parent().searchConfigBuilder().queryAnalyzer(new CJKAnalyzer()).build() ;

		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.deleteAll() ;
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20L).text("explain", "hello bleujin").update() ;
				isession.newDocument("hero").keyword("name", "hero").number("age", 30).text("explain", "hi hero").update() ;
				isession.newDocument("jin").keyword("name", "jin").number("age", 7).text("explain", "namaste jin").update() ;
				return null;
			}
		}) ;
		SearchRequest request = cen.newSearcher().createRequest("bleujin");
		Debug.line(request.query());
		request.find().debugPrint();
		
//		cen.newSearcher().createRequestByTerm("IS-all", "bleujin").find().debugPrint();
		
	}

}
