package net.ion.isearcher.lucene;

import java.io.File;
import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.indexer.write.Mutex;

import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

public class LockTest extends ISTestCase {

	public void testWriter() throws Exception {
		File file = getTestDirFile() ;
		
		FSDirectory dir = FSDirectory.open(file);
		IndexWriter writer = new IndexWriter(dir, new KoreanAnalyzer(), MaxFieldLength.LIMITED ) ;
		try {
			FSDirectory dir2 = FSDirectory.open(file);
			IndexWriter writer2 = new IndexWriter(dir2, new KoreanAnalyzer(), MaxFieldLength.LIMITED ) ;
			fail() ;
		} catch(LockObtainFailedException ignore){
		}
		IndexReader reader = writer.getReader() ;
	}


	public void testWriterReader() throws Exception {
		File file = getTestDirFile() ;
		
		FSDirectory dir = FSDirectory.open(file);
		IndexWriter writer = new IndexWriter(dir, new KoreanAnalyzer(), MaxFieldLength.LIMITED ) ;
//		IndexReader reader = writer.getReader() ;
//		writer.close() ;
//		
//		Collection fieldNames = reader.getFieldNames(FieldOption.ALL) ;
//		reader.deleteDocument(1) ;
//		Debug.debug(fieldNames) ;
		try {
			FSDirectory dir2 = FSDirectory.open(file);
			IndexWriter writer2 = new IndexWriter(dir2, new KoreanAnalyzer(), MaxFieldLength.LIMITED ) ;
			fail() ;
		} catch(LockObtainFailedException ignore){
		}
	}


	public void testWriterThread() throws Exception {
		final File file = getTestDirFile() ;
		
		FSDirectory dir = FSDirectory.open(file);
		IndexWriter writer1 = new IndexWriter(dir, new KoreanAnalyzer(), MaxFieldLength.LIMITED ) ;
		Thread another = new Thread(){
			public void run(){
				try {
					FSDirectory dir = FSDirectory.open(file);
					IndexWriter writer2 = new IndexWriter(dir, new KoreanAnalyzer(), MaxFieldLength.LIMITED ) ;
					fail() ;
					throw new IllegalStateException();
				} catch (IOException ignore) {
				}
			}
		} ;
		another.start() ;
		
		another.join() ;
	}
	
	
	public void testMutex() throws Exception {
		Mutex mutex = new Mutex();

		UserThread[] users = new UserThread[2];
		for (int i = 0; i < users.length; i++) {
			users[i] = new UserThread(mutex);
		}

		for (int i = 0; i < users.length; i++) {
			users[i].start();
		}

		for (int i = 0; i < users.length; i++) {
			users[i].join();
		}
	}

}

class UserThread extends Thread {

	private Mutex mutex;

	UserThread(Mutex mutex) {
		this.mutex = mutex;
	}

	public void run() {

		try {
			boolean isMyOwn = mutex.tryLock(this);
			Debug.debug("START");

			if (isMyOwn != true) {
				throw new LockObtainFailedException("fail");
			}
			Thread.sleep(2000);
			Debug.debug("END");
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				mutex.unLock(this, true);
			} catch (LockObtainFailedException e) {
				e.printStackTrace();
			}
		}

	}

}