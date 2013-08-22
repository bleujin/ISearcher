package net.ion.nsearcher.problem;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ObjectId;
import net.ion.nsearcher.ISTestCase;

import org.apache.lucene.analysis.debug.standard.DStandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class TestLockTimeout extends TestCase {

	private Directory dir;
	private ExecutorService es;
	private ExecutorService ses ;

	public void setUp() throws Exception {
		FileUtil.deleteDirectory(new File("resource/lock"));
		this.dir = new SimpleFSDirectory(new File("./resource/lock"));

		this.es = Executors.newCachedThreadPool();
		this.ses = Executors.newSingleThreadExecutor() ;

	}

	public void tearDown() throws Exception {
		es.shutdown();
		dir.close();
	}

	public void testReadNWrite() throws Exception {
		for (int i = 0; i < 1000; i++) {
			es.submit(new ReadJob(dir));
			ses.submit(new WriteJob(dir));
			Thread.sleep(100);
		}

		// readJob.close() ;
		Thread.sleep(1000);
	}

	public void testRead() throws Exception {
		for (int i = 0; i < 100; i++) {
			es.submit(new ReadJob(dir));
			Thread.sleep(200);
		}

		// readJob.close() ;
		Thread.sleep(1000);
	}

	public void testAppendWrite() throws Exception {
		final IndexWriter iwriter = new IndexWriter(dir, ISTestCase.testWriterConfig());

		for (int i = 0; i < 5000; i++) {
			final ReadJob readJob = new ReadJob(dir);
			es.submit(readJob);
			es.submit(new Callable<Void>() {
				public Void call() throws Exception {
					iwriter.addDocument(createDoc(new ObjectId().toString(), 1));
					iwriter.commit();
					return null;
				}
			});
			Thread.sleep(2000);
		}

	}

	public static Document createDoc(String groupKey, int i) {
		Document doc = new Document();
		doc.add(new Field("tranid", groupKey, Store.YES, Index.ANALYZED));
		doc.add(new IntField("index", i, Store.YES));
		doc.add(new Field("name", "bleujin", Store.YES, Index.ANALYZED));
		return doc;
	}

}

class ReadJob implements Callable<Void> {

	private Directory dir;

	public ReadJob(Directory dir) {
		this.dir = dir;
	}

	public Void call() throws Exception {
		IndexReader reader = null;
		IndexSearcher searcher = null;
		try {
			reader = IndexReader.open(dir);
			searcher = new IndexSearcher(reader);
			Debug.line(searcher.search(new MatchAllDocsQuery(), 1).totalHits);
		} finally {
			IOUtil.closeQuietly(reader);
		}

		return null;
	}

}

class WriteJob implements Callable<Void> {

	private Directory dir;

	public WriteJob(Directory dir) {
		this.dir = dir;
	}

	public Void call() throws Exception {
		final IndexWriterConfig wconf = new IndexWriterConfig(Version.LUCENE_36, new DStandardAnalyzer(Version.LUCENE_36));
		wconf.setWriteLockTimeout(3000);
		IndexWriter iw = null;
		try {
			iw = new IndexWriter(dir, wconf);
			String key = new ObjectId().toString();
			for (int i = 0; i < 3; i++) {
				iw.addDocument(createDoc(key, i));
			}
			iw.commit();
		} catch (Throwable e) {
			if (iw != null)
				iw.rollback();
			e.printStackTrace();
		} finally {
			IOUtil.closeQuietly(iw);
		}
		return null;
	}

	private Document createDoc(String groupKey, int i) {
		Document doc = new Document();
		doc.add(new Field("tranid", groupKey, Store.YES, Index.ANALYZED));
		doc.add(new IntField("index", 1, Store.YES));
		doc.add(new Field("name", "bleujin", Store.YES, Index.ANALYZED));
		return doc;
	}
}
