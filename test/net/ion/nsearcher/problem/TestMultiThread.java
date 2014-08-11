package net.ion.nsearcher.problem;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexJobs;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.reader.InfoReader.InfoHandler;
import net.ion.nsearcher.search.Searcher;
import junit.framework.TestCase;

public class TestMultiThread extends TestCase {

	
	public void testDeadLock() throws Exception {
		final Central c = CentralConfig.newRam().indexConfigBuilder().setExecutorService(Executors.newCachedThreadPool()).build();
		
		Searcher searcher = c.newSearcher() ;
		InfoReader reader = c.newReader() ;
		c.newIndexer().index(IndexJobs.create("bleujin", 3)) ;
		
		reader.info(new InfoHandler<Void>() {
			@Override
			public Void view(IndexReader ireader, DirectoryReader dreader) throws IOException {
				c.newIndexer().index(IndexJobs.create("hero", 2)) ;
				Debug.line(ireader.maxDoc(), dreader.getIndexCommit());
				return null;
			}
		}) ;
		
		
		searcher.search("").debugPrint(); 
	}
	
	
	public void testLongTimeIndex() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		central.newIndexer().index(IndexJobs.create("before", 1)) ;
		Searcher searcher = central.newSearcher() ;
		assertEquals(1, searcher.search("").size()) ;
		long start = System.currentTimeMillis() ;
		
		central.newIndexer().asyncIndex(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument wdoc = isession.newDocument("after") ;
				Thread.sleep(4000);
				isession.updateDocument(wdoc) ;
				return null;
			}
		}) ;
		
		Thread.sleep(100);
		assertEquals(1, searcher.search("").size()) ;
		assertEquals(true, System.currentTimeMillis() - start < 200); // not wait 
	}
	
	
	
	
	public void testSimulDeadLock() throws Exception {
		ReentrantReadWriteLock locker = new ReentrantReadWriteLock() ;
		
		ReadLock rlock = locker.readLock() ;
		rlock.lock(); 
		
		WriteLock wlock = locker.writeLock() ;
		wlock.lock(); 
		
		rlock.unlock(); 
		wlock.unlock();
		
		Debug.line();
	}
	
	
	public void testSimulNormal() throws Exception {
		final ReentrantReadWriteLock locker = new ReentrantReadWriteLock() ;
		
		ExecutorService es = Executors.newCachedThreadPool() ;

		for (int i = 0; i < 1000; i++) {
			es.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					ReadLock rlock = locker.readLock() ;
					try {
						rlock.lock();
						Thread.sleep(RandomUtil.nextInt(5));
					} finally {
						rlock.unlock(); 
						System.out.print('r');
					}
					return null;
				}
			}) ;
			
			es.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					WriteLock rlock = locker.writeLock() ;
					try {
						rlock.lock();
						Thread.sleep(RandomUtil.nextInt(30));
					} finally {
						rlock.unlock(); 
						System.out.print('w');
					}
					return null;
				}
			}) ;
			
		}
		
		es.awaitTermination(100, TimeUnit.SECONDS) ;
		Debug.line();
	}
	
	
	public void testReadsWrite() throws Exception {

		ExecutorService outer = Executors.newCachedThreadPool();

		ExecutorService es = Executors.newCachedThreadPool();
		// ExecutorService es = Executors.newSingleThreadExecutor() ; 
		final Central c = CentralConfig.newRam().indexConfigBuilder().setExecutorService(es).build();

		for (int i = 0; i < 100; i++) {
			outer.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					c.newIndexer().index(new IndexJob<Void>() {
						@Override
						public Void handle(IndexSession isession) throws Exception {
							for (int i = 0; i < RandomUtil.nextInt(5); i++) {
								int idx = RandomUtil.nextInt(10);
								WriteDocument wdoc = isession.newDocument(idx + "");
								wdoc.number("idx", idx);
								isession.updateDocument(wdoc);
								Thread.sleep(RandomUtil.nextInt(5));
							}
							return null;
						}
					});
					return null;
				}
			});
			Thread.sleep(RandomUtil.nextInt(10));
		}

		for (int i = 0; i < 1000; i++) {
			outer.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					Searcher searcher = c.newSearcher();
//					searcher.search(RandomUtil.nextInt(10) + "").size() ;
					 Debug.line(searcher.search(RandomUtil.nextInt(10) + "").size());
					return null;
				}
			});
			Thread.sleep(RandomUtil.nextInt(20));
		}
		
		new InfinityThread().startNJoin(); 

	}
}
