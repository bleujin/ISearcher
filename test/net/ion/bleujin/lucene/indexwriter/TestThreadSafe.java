package net.ion.bleujin.lucene.indexwriter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ko.DStandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class TestThreadSafe extends TestCase{

	
	public void testThreadSafe() throws Exception {
		Directory dir = new RAMDirectory() ;
		Analyzer analyzer = new DStandardAnalyzer(SearchConstant.LuceneVersion);
		
		IndexWriterConfig wconfig = new IndexWriterConfig(SearchConstant.LuceneVersion, analyzer);
		wconfig.setOpenMode(OpenMode.CREATE_OR_APPEND) ;
		
		final IndexWriter empty = new IndexWriter(dir, wconfig);
		empty.close() ;
		
		final IndexWriter writer = new IndexWriter(dir, wconfig);
		Callable<Void> c1 = new Callable<Void>() {
			public Void call() throws Exception {
				Document doc = new Document();
				doc.add(new Field("thread", "1 th", Store.YES, Index.NOT_ANALYZED)) ;
				writer.addDocument(doc) ;
				writer.commit() ;
				writer.close() ;
				return null;
			}
		};

		Callable<Void> c2 = new Callable<Void>() {
			public Void call() throws Exception {
				Document doc = new Document();
				doc.add(new Field("thread", "2 th", Store.YES, Index.NOT_ANALYZED)) ;
				writer.addDocument(doc) ;
				
				writer.rollback() ;
				writer.close() ;
				return null;
			}
		};

		ExecutorService es = Executors.newFixedThreadPool(2);
		
		es.submit(c1) ;
		es.submit(c2) ;
		Thread.sleep(1000) ;
		es.shutdown() ;
		es.awaitTermination(1, TimeUnit.SECONDS) ;
		
		IndexSearcher searcher = new IndexSearcher(IndexReader.open(dir));
		TopDocs docs = searcher.search(new MatchAllDocsQuery(), 10);
		
		for (ScoreDoc sdoc : docs.scoreDocs) {
			Debug.line(searcher.doc(sdoc.doc).getField("thread")) ;
		}
		
		
	}
}
