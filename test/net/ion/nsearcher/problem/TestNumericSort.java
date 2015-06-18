package net.ion.nsearcher.problem;

import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

public class TestNumericSort extends TestCase {

	private Central cen;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.cen = CentralConfig.newRam().build();

		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 5; i++) {
					isession.newDocument("" + i).keyword("str", "" + i).number("num", i).insert();
				}
				return null;
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		cen.close();
		super.tearDown();
	}
	
	public void testAscending() throws Exception {
		cen.newSearcher().createRequest("").sort("num").find().debugPrint(); 
	}

	
	
	public void testLucene() throws Exception {
		RAMDirectory dir = new RAMDirectory() ;

		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig wconfig = new IndexWriterConfig(Version.LUCENE_44, analyzer);
		IndexWriter iw = new IndexWriter(dir, wconfig) ;
		iw.addDocuments(makeDocuments());
		iw.commit(); 
		iw.close();
		
		DirectoryReader dreader = DirectoryReader.open(dir) ;
		IndexSearcher searcher = new IndexSearcher(dreader) ;
		TopDocs topdocs = searcher.search(new MatchAllDocsQuery(), 10, new Sort(new SortField("num", SortField.Type.LONG, true)));
		
		for(ScoreDoc d : topdocs.scoreDocs ) {
			Document doc = dreader.document(d.doc) ;
			Debug.line(doc, doc.get("num"));
		}
		
		dreader.close(); 
		dir.close();
	}

	private List<Document> makeDocuments() {
		List<Document> result = ListUtil.newList() ;
		for (int i = 0; i < 5 ; i++) {
			Document doc = new Document() ;
			doc.add(new StringField("num", ""+ RandomUtil.nextInt(10), Store.YES));
			result.add(doc) ;
		}
		return result;
	}
	
}
