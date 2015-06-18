package net.ion.bleujin.lucene.indexwriter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;

import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

public class LockTest extends ISTestCase {

	public void testWriter() throws Exception {
		File file = getTestDirFile() ;
		
		FSDirectory dir = FSDirectory.open(file);
		IndexWriter writer = testWriter(dir, new KoreanAnalyzer()) ;
		try {
			FSDirectory dir2 = FSDirectory.open(file);
			IndexWriter writer2 = testWriter(dir2, new KoreanAnalyzer()) ;
			fail() ;
		} catch(LockObtainFailedException ignore){
		}
		Debug.debug() ;
	}


	public void testWriterReader() throws Exception {
		File file = getTestDirFile() ;
		
		FSDirectory dir = FSDirectory.open(file);
		IndexWriter writer = testWriter(dir, new KoreanAnalyzer()) ;
//		IndexReader reader = writer.getReader() ;
//		writer.close() ;
//		
//		Collection fieldNames = reader.getFieldNames(FieldOption.ALL) ;
//		reader.deleteDocument(1) ;
//		Debug.debug(fieldNames) ;
		try {
			FSDirectory dir2 = FSDirectory.open(file);
			IndexWriter writer2 = testWriter(dir2, new KoreanAnalyzer()) ;
			fail() ;
		} catch(LockObtainFailedException ignore){
		}
	}


	public void testWriterThread() throws Exception {
		final File file = getTestDirFile() ;
		
		FSDirectory dir = FSDirectory.open(file);
		IndexWriter writer1 = testWriter(dir, new KoreanAnalyzer()) ;
		Thread another = new Thread(){
			public void run(){
				try {
					FSDirectory dir = FSDirectory.open(file);
					IndexWriter writer2 = testWriter(dir, new KoreanAnalyzer()) ;
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
class Mutex {

	private Object owner ;
	private boolean isUpdated;
	private ReentrantLock locker = new ReentrantLock();

	public synchronized boolean tryLock(Object owner) {
		if (this.owner != null && this.owner != owner) {
			return false ;
		}
		
		if (this.owner == owner && locker.isLocked()) return true ;
		
		this.owner = owner ;
		boolean result = locker.tryLock();
		return result;
	}

	public synchronized void lock(Object owner) throws LockObtainFailedException {
		if (this.owner != null) throw new LockObtainFailedException("exception.indexer.lock.obtain_failed:owner[" + this.owner + "]");
		this.owner = owner ;
		locker.lock();
		
	}

	public synchronized void unLock(Object owner, boolean modified) throws LockObtainFailedException {
		if (this.owner == null) return ;
		if (this.owner != owner) {
			Debug.line(this.owner, owner) ;
			throw new LockObtainFailedException("exception.indexer.lock.release_failed:not_owner[current:" + this.owner + "]");
		}
		locker.unlock();
		this.owner = null ;
		isUpdated = modified ;
	}

	public synchronized boolean isUpdated() {
		return isUpdated;
	}

	public synchronized void reflectUpdate() {
		isUpdated = false ;
	}

	public synchronized boolean isOwner(IndexWriter owner) throws LockObtainFailedException {
		if (this.owner == null) {
//			this.owner = owner ;
			throw new LockObtainFailedException("exception.indexer.lock.obtail_failed:no lock acquired"); 
		}
		return this.owner == owner ;
	}
	
	public Object getOwner(){
		return owner ;
	}

}
