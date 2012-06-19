package net.ion.bleujin;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.DaemonIndexer;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.impl.JobEntry;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.SearchResponse;
import net.ion.isearcher.searcher.processor.PostProcessor;
import net.ion.isearcher.searcher.processor.SearchTask;

import org.apache.lucene.analysis.Analyzer;
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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

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

		RAMDirectory dir = new RAMDirectory();
		final Central c = Central.createOrGet(dir);
		

		ExecutorService exec = Executors.newFixedThreadPool(5);
		Runnable[] runs = new Runnable[10];
		for (int i : ListUtil.rangeNum(runs.length)) {
			runs[i] = new Runnable() {
				public void run() {
					DaemonIndexer indexer = c.newDaemonHander();
					indexer.addIndexJob(new JobEntry<Boolean>() {
						public Analyzer getAnalyzer() {
							return new MyKoreanAnalyzer();
						}

						public Boolean handle(IWriter writer) throws IOException {
							try {
								for (int i : ListUtil.rangeNum(10)) {
									MyDocument doc = MyDocument.testDocument();
									doc.add(MyField.number("mindex", i));
									doc.add(MyField.keyword("name", "bleujin"));
									writer.insertDocument(doc);
								}
								
								writer.commit() ;
								
								 { // delete
									 
									 Query delQuery = new QueryParser(Version.LUCENE_36, "mindex", new StandardAnalyzer(Version.LUCENE_36)).parse("mindex:[3 TO 4]");
									 // Query delQuery = new TermQuery(new Term("mindex", "3")) ;
									 
									 writer.deleteQuery(delQuery) ;
									 writer.commit() ;
								 }
								long start = System.currentTimeMillis();
								ISearcher searcher = c.newSearcher();
								SearchResponse sr = searcher.searchTest("name:bleujin");
								Debug.line(start, System.currentTimeMillis(), sr.getTotalCount());

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
			ISearcher searcher = c.newSearcher();
			searcher.addPostListener(new PostProcessor() {
				public void postNotify(SearchTask searchTask) {
					Debug.line(searchTask.getResult().elapsedTime()) ;
				}
			}) ;
			
			ISearchRequest req = SearchRequest.create("name:bleujin", "mindex", new MyKoreanAnalyzer());
			SearchResponse res = searcher.search(req);
			Debug.line(System.currentTimeMillis(), res.getTotalCount(), res.getDocument());
			
			
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
