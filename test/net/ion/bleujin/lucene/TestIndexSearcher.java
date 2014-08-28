package net.ion.bleujin.lucene;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.search.filter.MatchAllDocsFilter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class TestIndexSearcher extends TestCase  {

	
	public void testSearch() throws Exception {
		Version version = Version.LUCENE_44;
		Analyzer anal = new StandardAnalyzer(version);
		
		Directory dir = writeSample(version, anal); 
		
		DirectoryReader dreader = DirectoryReader.open(dir) ;
		IndexSearcher searcher = new IndexSearcher(dreader) ;
		
		Query query = new MatchAllDocsQuery() ;
		int n = 101 ;
		Sort sort = new Sort(new SortField("index", Type.INT)) ;
		Filter filter = new MatchAllDocsFilter() ;
		TopDocs tdocs = searcher.searchAfter(null, query, filter, n, sort) ;
		
		printDoc(searcher, tdocs);
	}

	
	public void testAfterSearch() throws Exception {
		Version version = Version.LUCENE_44;
		Analyzer anal = new StandardAnalyzer(version);
		
		Directory dir = writeSample(version, anal); 
		
		DirectoryReader dreader = DirectoryReader.open(dir) ;
		IndexSearcher searcher = new IndexSearcher(dreader) ;
		
		Query query = new MatchAllDocsQuery() ;
		int n = 101 ;
		Sort sort = new Sort(new SortField("index", Type.INT)) ;
		Filter filter = new MatchAllDocsFilter() ;
		
		TopDocs tdocs = searcher.searchAfter(null, query, filter, n, sort) ;
		
		ScoreDoc[] sdocs = tdocs.scoreDocs ;
		ScoreDoc lastdoc = sdocs[sdocs.length-2] ;
		
		
		TopDocs afterTdocs = searcher.searchAfter(lastdoc, query, filter, n, sort) ;
		
		printDoc(searcher, afterTdocs);
	}
	
	
	public void testTotalHit() throws Exception {
		Version version = Version.LUCENE_44;
		Analyzer anal = new StandardAnalyzer(version);
		
		Directory dir = writeSample(version, anal); 
		DirectoryReader dreader = DirectoryReader.open(dir) ;
		IndexSearcher searcher = new IndexSearcher(dreader) ;
		
		TopDocs tdocs = searcher.search(new MatchAllDocsQuery(), 100) ;
		Debug.line(tdocs.totalHits, tdocs.scoreDocs.length);
	}
	
	
	public void testPhantomRead() throws Exception {

		Version version = Version.LUCENE_44;
		Analyzer anal = new StandardAnalyzer(version);
		
		Directory dir = writeSample(version, anal); 
		DirectoryReader dreader = DirectoryReader.open(dir) ;
		IndexSearcher searcher = new IndexSearcher(dreader) ;
		TopDocs tdocs = searcher.search(new MatchAllDocsQuery(), 100) ;
		assertEquals(150, tdocs.totalHits);

		writeSample(version, anal, dir) ;
		
		tdocs = searcher.search(new MatchAllDocsQuery(), 100) ;
		assertEquals(150, tdocs.totalHits);
		
		
		dreader = DirectoryReader.open(dir) ;
		searcher = new IndexSearcher(dreader) ;
		tdocs = searcher.search(new MatchAllDocsQuery(), 100) ;
		assertEquals(300, tdocs.totalHits);
	}
	
	
	public void testDirectoryOpenSpeed() throws Exception {
		Version version = Version.LUCENE_44;
		Analyzer anal = new StandardAnalyzer(version);
		
		Directory dir = writeSample(version, anal);
		DirectoryReader dreader = DirectoryReader.open(dir) ;

		long start = System.currentTimeMillis() ;
		for (int i = 0; i < 1000; i++) {
			DirectoryReader newReader = DirectoryReader.openIfChanged(dreader) ;
			if (newReader != null) dreader = newReader  ;
		}
		Debug.line(System.currentTimeMillis() - start);
	}
	

	private void printDoc(IndexSearcher searcher, TopDocs tdocs) throws IOException {
		List<Document> docs = ListUtil.newList() ;
		ScoreDoc[] sdocs = tdocs.scoreDocs ;
		for (ScoreDoc doc : sdocs) {
			docs.add(searcher.doc(doc.doc)) ;
		}
		
		Debug.line(docs.size(), docs);
	}
	
	private Directory writeSample(Version version, Analyzer anal) throws IOException {
		Directory dir = new RAMDirectory() ;
		return writeSample(version, anal, dir);
	}


	private Directory writeSample(Version version, Analyzer anal, Directory dir) throws IOException {
		IndexWriter iwriter = new IndexWriter(dir, new IndexWriterConfig(version, anal)) ;
		for (int i = 0; i < 150; i++) {
			Document doc = new Document();
			doc.add(new IntField("index", i, Store.YES));
			doc.add(new StringField("name", "bleujin", Store.YES));
			iwriter.addDocument(doc);
		}
		iwriter.commit(); 
		iwriter.close();
		return dir;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
