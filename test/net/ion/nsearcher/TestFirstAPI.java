package net.ion.nsearcher;

import java.io.IOException;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectId;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class TestFirstAPI extends TestCase {

	private Central cen ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.cen = CentralConfig.newRam().build() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		cen.close() ;
		super.tearDown();
	}
	
	public void testCreateSearcher() throws Exception {
		Searcher searcher = cen.newSearcher();
		assertEquals(0, searcher.search("").size()) ; 
	}

	public void testCreateIndexer() throws Exception {
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new StandardAnalyzer(SearchConstant.LuceneVersion), new IndexJob<Void>(){
			public Void handle(IndexSession session) throws IOException {
				for (int i : ListUtil.rangeNum(10)) {
					WriteDocument doc = WriteDocument.newDocument(new ObjectId().toString()).add(JsonObject.create().put("name", "bleujin").put("age", i));
					session.insertDocument(doc) ;
				}
				return null;
			}
		}) ;
		
		Searcher searcher = cen.newSearcher();
		assertEquals(10, searcher.search("").size()) ;
	}
	
	
	
	
	
	
	
}
