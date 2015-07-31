package net.ion.nsearcher.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import junit.framework.TestCase;

public class TestSearchRequestCollect extends TestCase {

	
	private Central central;
	private Searcher searcher;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.central = CentralConfig.newRam().build() ;
		this.searcher = central.newSearcher() ;

		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 105; i++) {
					isession.newDocument("" + i).number("i", i).update() ;
				}
				return null;
			}
		}) ;
	}

	public void testCollect() throws Exception {
		SearchResponse response = central.newSearcher().createRequest("").selections("IS-Key", "i").collect(new AbstractDocCollector(){
			@Override
			public ColResult accept(ReadDocument doc) {
				return doc.asLong("i", 0) >= 25 && doc.asLong("i", 0) <= 35 ? ColResult.ACCEPT : ColResult.REVOKE ;
			}
		}).page(Page.create(2, 1)).find() ;
		
		response.debugPrint();
		Debug.line(response.totalCount(), response.size(), response.getDocument().size());
	}
	
	
	public void testPageCollector() throws Exception {
		final AbstractDocCollector bullhock = new AbstractDocCollector(){
			@Override
			public ColResult accept(ReadDocument doc) {
				return doc.asLong("i", 0) >= 40 && doc.asLong("i", 0) <= 50 ? ColResult.ACCEPT : ColResult.REVOKE ;
			}
		};

		SearchRequest request = central.newSearcher().createRequest("").selections("IS-Key", "i").collect(new PageCollector(Page.create(3, 3), bullhock));
		SearchResponse response = request.find() ;

		response.debugPrint(); 
		Debug.line(response.totalCount(), response.size(), response.getDocument().size());
	}
}
