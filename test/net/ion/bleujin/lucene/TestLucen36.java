package net.ion.bleujin.lucene;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;
import net.ion.nsearcher.search.processor.PostProcessor;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class TestLucen36 extends TestCase {

	public void testIndexReader() throws Exception {
		RAMDirectory dir = new RAMDirectory();

		IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_36, new MyKoreanAnalyzer());
		IndexWriter indexer = new IndexWriter(dir, writerConfig);

		indexer.commit();

		IndexReader reader = IndexReader.open(dir);

		{
			// add
			for (int i : ListUtil.rangeNum(10)) {
				Document doc = new Document();
				doc.add(new Field("name", "bleujin", Store.YES, Index.ANALYZED_NO_NORMS));
				indexer.addDocument(doc);
			}
			indexer.commit();
		}

		IndexCommit commit = reader.getIndexCommit();

		IndexReader newReader = ObjectUtil.coalesce(IndexReader.openIfChanged(reader), reader);

		Debug.line(reader, newReader, commit);
		IndexSearcher searcher = new IndexSearcher(newReader);

		Query query = new QueryParser(Version.LUCENE_36, "name", new MyKoreanAnalyzer()).parse("name:bleujin");
		TopDocs docs = searcher.search(query, 10);

		for (ScoreDoc doc : docs.scoreDocs) {
			Debug.line(newReader.document(doc.doc));
		}

	}

	public void testMyThread() throws Exception {

		final Central c = CentralConfig.newRam().build() ;
		

		ExecutorService exec = Executors.newFixedThreadPool(5);
		Runnable[] runs = new Runnable[10];
		for (int i : ListUtil.rangeNum(runs.length)) {
			runs[i] = new Runnable() {
				public void run() {
					Indexer indexer = c.newIndexer();
					indexer.index(new IndexJob<Boolean>() {

						public Boolean handle(IndexSession writer) throws IOException {
							try {
								for (int i : ListUtil.rangeNum(10)) {
									MyDocument doc = MyDocument.testDocument();
									doc.add(MyField.number("mindex", i));
									doc.add(MyField.keyword("name", "bleujin"));
									writer.insertDocument(doc);
								}
								
								 { // delete
									 
									 Query delQuery = new QueryParser(Version.LUCENE_36, "mindex", new StandardAnalyzer(Version.LUCENE_36)).parse("mindex:[3 TO 4]");
									 // Query delQuery = new TermQuery(new Term("mindex", "3")) ;
									 writer.deleteQuery(delQuery) ;
								 }
								long start = System.currentTimeMillis();
								Searcher searcher = c.newSearcher();
								SearchResponse sr = searcher.search("name:bleujin");
								Debug.line(start, System.currentTimeMillis(), sr.totalCount());

								return true;
							} catch (ParseException ex) {
								throw new IOException(ex.getMessage());
							}
						}

						public void onException(Throwable ex) {
							ex.printStackTrace();
						}
					});

				}
			};
			exec.execute(runs[i]);
		}

		Thread.sleep(1500);

		Debug.line();

		// Query delQuery = new QueryParser(Version.LUCENE_36, "mindex", new MyKoreanAnalyzer()).parse("name:bleujin");
		// indexer.deleteDocuments(delQuery);
		// indexer.commit();

		for (int i = 0; i < 5; i++) {
			Searcher searcher = c.newSearcher();
			searcher.addPostListener(new PostProcessor() {
				public void postNotify(SearchRequest req, SearchResponse res) {
					Debug.line(res.elapsedTime()) ;
				}
			}) ;
			
			SearchResponse res = searcher.createRequest("name:bleujin", new MyKoreanAnalyzer()).ascending("mindex") .find();
			Debug.line(System.currentTimeMillis(), res.totalCount(), res.getDocument());
			
			
		}

		exec.shutdown();

	}

	public void testThread() throws Exception {

		final RAMDirectory dir = new RAMDirectory();
		IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_36, new MyKoreanAnalyzer());
		final IndexWriter indexer = new IndexWriter(dir, writerConfig);
		indexer.commit();

		final IndexReader reader = IndexReader.open(dir);

		ExecutorService exec = Executors.newFixedThreadPool(5);

		Runnable[] runs = new Runnable[10];
		for (int i : ListUtil.rangeNum(runs.length)) {
			runs[i] = new Runnable() {
				public void run() {
					try {
						// IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_36, new MyKoreanAnalyzer());
						// final IndexWriter indexer = new IndexWriter(dir, writerConfig);
						long start = System.currentTimeMillis();
						for (int i : ListUtil.rangeNum(10)) {
							Document doc = new Document();
							NumericField field = new NumericField("mindex");
							field.setIntValue(i);
							doc.add(field);
							doc.add(new Field("name", "bleujin", Store.YES, Index.NOT_ANALYZED));
							doc.add(new Field("cname", "bleujin" + i, Store.YES, Index.NOT_ANALYZED));
							indexer.addDocument(doc);
						}
						indexer.commit();

						// { // delete
						// Query delQuery = new QueryParser(Version.LUCENE_36, "mindex", new MyKoreanAnalyzer()).parse("mindex:3");
						// indexer.deleteDocuments(delQuery);
						// indexer.commit();
						// }

						IndexReader newReader = ObjectUtil.coalesce(IndexReader.openIfChanged(reader), reader);
						IndexSearcher searcher = new IndexSearcher(newReader);
						Query query = new QueryParser(Version.LUCENE_36, "name", new MyKoreanAnalyzer()).parse("name:bleujin");
						TopDocs docs = searcher.search(query, 10000);

						Debug.line(start, System.currentTimeMillis(), docs.totalHits, reader == newReader);
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (ParseException ex) {
						ex.printStackTrace();
					}
				}
			};
			exec.execute(runs[i]);
		}

		Thread.sleep(3000);

		Debug.line();

		Query delQuery = new QueryParser(Version.LUCENE_36, "mindex", new MyKoreanAnalyzer()).parse("name:bleujin");
		indexer.deleteDocuments(delQuery);
		indexer.commit();

		for (int i = 0; i < 5; i++) {
			IndexReader newReader = ObjectUtil.coalesce(IndexReader.openIfChanged(reader), reader);
			IndexSearcher searcher = new IndexSearcher(newReader);
			Query query = new QueryParser(Version.LUCENE_36, "mindex", new MyKoreanAnalyzer()).parse("name:bleujin");
			TopDocs docs = searcher.search(query, 10000, new Sort(new SortField("mindex", SortField.INT, true)));
			ScoreDoc[] sdocs = docs.scoreDocs;
			for (int j = 0; j < Math.min(1, sdocs.length); j++) {
				Debug.line(docs.totalHits, System.currentTimeMillis(), sdocs.length, newReader.document(sdocs[0].doc));
			}
		}

		exec.shutdown();

	}

}

class Writer extends Thread {

}
