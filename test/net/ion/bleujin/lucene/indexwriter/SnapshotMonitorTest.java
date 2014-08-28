package net.ion.bleujin.lucene.indexwriter;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.LockObtainFailedException;

public class SnapshotMonitorTest extends ISTestCase{

	
	private Central central = null ;
	private int maxCount = 20;
	
	public void setUp() throws Exception {
		this.central = CentralConfig.newRam().build() ;
	}
	
	
	
	public void testReadWriting() throws Exception {
		
		Thread writerThread = new Thread(new WriterThread(central, maxCount)) ;
		writerThread.start() ;

		Thread readerThread = new Thread(new ReaderThread(central, maxCount)) ;
		readerThread.start() ;

		Thread newreaderThread = new Thread(new NewReaderThread(central, maxCount)) ;
		newreaderThread.start() ;
		

		writerThread.join() ;
		readerThread.join() ;
		newreaderThread.join() ;
	}

	


	public void testWriteWrite() throws Exception {
		int threadCount = 5;
		Thread[] writerThread = new Thread[threadCount] ;
		for (int i = 0; i < threadCount; i++) {
			writerThread[i] = new Thread(new OnlyWriterThread(maxCount, central)) ;
			writerThread[i].start() ;
		}

		for (int i = 0; i < threadCount; i++) {
			writerThread[i].join() ;
		}
		
		new ReaderThread(central, 1).run() ;
	}
	
	
	public void testSelfWrite() throws Exception {
		
		int threadCount = 5;
		Thread[] writerThread = new Thread[threadCount] ;
		for (int i = 0; i < threadCount; i++) {
			writerThread[i] = new Thread(new SelfWriterThread(central, maxCount)) ;
			writerThread[i].start() ;
		}

		for (int i = 0; i < threadCount; i++) {
			writerThread[i].join() ;
		}
		
		new ReaderThread(central, 1).run() ;
	}
	
}

class ReaderThread implements Runnable {

	private Central central ;
	private int maxCount ;
	ReaderThread(Central central, int maxCount){
		this.central = central ;
		this.maxCount = maxCount ;
	}
	
	public void run() {
		IndexReader indexReader = null ;
		try {
			Thread.sleep(100) ;
			indexReader = central.newReader().getIndexReader() ;
			for (int i = 0; i < maxCount; i++) {
				int docCount = indexReader.maxDoc() ;
				Debug.debug("Reader : " + docCount) ;
				Thread.sleep(70) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeQuietly(indexReader) ;
		}
	}
}


class NewReaderThread implements Runnable {

	private Central central ;
	private int maxCount ;
	NewReaderThread(Central central, int maxCount){
		this.central = central ;
		this.maxCount = maxCount ;
	}
	
	public void run() {
		IndexReader reader = null ;
		try {
			Thread.sleep(100) ;
			for (int i = 0; i < maxCount; i++) {
				reader = central.newReader().getIndexReader() ;
				int docCount = reader.maxDoc() ;
				Debug.debug("New Reader : " + docCount) ;
				IOUtil.closeQuietly(reader) ;
				Thread.sleep(70) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeQuietly(reader) ;
		}
	}
}


class SelfWriterThread implements Runnable {

	private int maxCount ;
	private Central central ;

	SelfWriterThread(Central central, int maxCount) throws CorruptIndexException, LockObtainFailedException, IOException{
		this.maxCount = maxCount ;
		this.central = central ;
	}
	
	public void run() {
		Indexer indexer = central.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument[] docs = ISTestCase.makeTestDocument(isession, maxCount) ;
				for (WriteDocument doc : docs) {
					isession.insertDocument(doc) ;
					try {
						Thread.sleep(50) ;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Debug.debug(doc.asString("NAME")) ;
					if (RandomUtil.nextBoolean()) isession.commit() ; 
					else isession.rollback() ;
				}
				return null ;
			}
		}) ;
	}
	

}




class OnlyWriterThread implements Runnable {

	private int maxCount ;
	private Central central ;

	OnlyWriterThread(int maxCount, Central central){
		this.maxCount = maxCount ;
		this.central = central ;
	}
	
	public void run() {
		Indexer indexer = central.newIndexer() ;
		indexer.index(new IndexJob<Void>() {

			public Void handle(IndexSession isession) throws Exception {
				WriteDocument[] docs = ISTestCase.makeTestDocument(isession, maxCount) ;
				for (WriteDocument doc : docs) {
					isession.insertDocument(doc) ;
					try {
						Thread.sleep(50) ;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Debug.debug(doc.asString("NAME")) ;
					if (RandomUtil.nextBoolean()) isession.commit() ; 
					else isession.rollback() ;
				}
				return null ;
			}
		}) ;
	}

}


class WriterThread implements Runnable {

	private Central central ;
	private int maxCount ;
	WriterThread(Central central, int maxCount){
		this.central = central ;
		this.maxCount = maxCount ;
	}
	
	public void run() {
		
		Indexer indexer = central.newIndexer() ;
		
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument[] docs = ISTestCase.makeTestDocument(isession, maxCount) ;
				
				for (final WriteDocument doc : docs) {
					isession.insertDocument(doc) ;
					try {
						Thread.sleep(50) ;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Debug.debug(doc.asString("subject")) ;
				}
				
				
				return null;
			}
		}) ;
	}
	
}

