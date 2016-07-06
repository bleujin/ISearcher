package net.ion.nsearcher.index;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.Searcher;
import junit.framework.TestCase;

public class TestSpecialChar extends TestCase {
	
	public void testIndex() throws Exception {
		Central central = CentralConfig.newRam()
					.searchConfigBuilder().queryAnalyzer(new KeywordAnalyzer()).build() ;
				//	.indexConfigBuilder().indexAnalyzer(new KeywordAnalyzer()).build() ;
		
		Indexer indexer = central.newIndexer();
		
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().keyword("name", "bleujin").keyword("codename", "H & P").unknown("codename", "H & P").insert() ;
				return null;
			}
		}) ;
		
		Searcher searcher = central.newSearcher() ;
		final String query = "name:bleujin AND codename:\"H & P\"";
		SearchRequest req = searcher.createRequest(query) ;
		req.find().debugPrint(); ;
		
		Debug.line();
		Debug.line(query, req);
		
		
		
		ReadDocument rdoc = searcher.createRequest("").findOne() ;
		Debug.line(rdoc.fields("codename"));
		
		
		central.destroySelf(); 
	}

}
