package net.ion.nsearcher.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.surround.parser.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import junit.framework.TestCase;

public class TestMultiReader extends TestCase {

	private Central c1;
	private Central c2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.c1 = CentralConfig.newRam().build();
		c1.newIndexer().index(IndexJob.SAMPLE);

		this.c2 = CentralConfig.newRam().build();
		c2.newIndexer().index(IndexJob.SAMPLE);
	}
	
	@Override
	protected void tearDown() throws Exception {
		c1.close(); 
		c2.close(); 
		super.tearDown();
	}

	public void testCreate() throws Exception {
		assertEquals(1, c1.newSearcher().search("").size());
		assertEquals(1, c2.newSearcher().search("").size());
	}

	public void testSearchLucene() throws Exception {
		MultiReader mreader = new MultiReader( new IndexReader[]{c1.newReader().getIndexReader(), c2.newReader().getIndexReader()});
		IndexSearcher isearcher = new IndexSearcher(mreader);

		Query query = new MatchAllDocsQuery();
		TopDocs tdoc = isearcher.search(query, 100);
		ScoreDoc[] sdoc = tdoc.scoreDocs;
		for (ScoreDoc d : sdoc) {
			Document fdoc = isearcher.doc(d.doc);
			Debug.line(fdoc);
		}
		
		c1.newSearcher().search("").debugPrint(); 
	}
	
	public void testInterface() throws Exception {
//		c1.newSearcher(c2) ;
		
		
	}

}
