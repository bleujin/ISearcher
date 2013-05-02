package net.ion.bleujin.lucene.indexwriter;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.WriteDocument;

import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;

public class SnapshotMonitorTest extends ISTestCase{

	
	private Directory dir = null ;
	private int maxCount = 20;
	
	public void setUp() throws Exception {
		dir = new RAMDirectory() ;
	}
	
	
	
	public void testReadWriting() throws Exception {
		
		createInit() ;
		
		Thread writer = new Thread(new WriterThread(dir, maxCount)) ;
		writer.start() ;

		Thread reader = new Thread(new ReaderThread(dir, maxCount)) ;
		reader.start() ;

		Thread newreader = new Thread(new NewReaderThread(dir, maxCount)) ;
		newreader.start() ;
		

		writer.join() ;
		reader.join() ;
		newreader.join() ;
	}

	
	private void createInit() throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexWriter writer = new IndexWriter(dir, new KoreanAnalyzer(), true, MaxFieldLength.LIMITED) ;
		writer.commit();
		writer.close() ;
	}



	public void testWriteWrite() throws Exception {
		IndexWriter writer = new IndexWriter(dir, new KoreanAnalyzer(), IndexWriter.MaxFieldLength.LIMITED);
		
		
		int threadCount = 5;
		Thread[] writerThread = new Thread[threadCount] ;
		for (int i = 0; i < threadCount; i++) {
			writerThread[i] = new Thread(new OnlyWriterThread(maxCount, writer)) ;
			writerThread[i].start() ;
		}

		for (int i = 0; i < threadCount; i++) {
			writerThread[i].join() ;
		}
		
		new ReaderThread(dir, 1).run() ;
	}
	
	
	public void testSelfWrite() throws Exception {
		
		int threadCount = 5;
		Thread[] writerThread = new Thread[threadCount] ;
		for (int i = 0; i < threadCount; i++) {
			writerThread[i] = new Thread(new SelfWriterThread(dir, maxCount)) ;
			writerThread[i].start() ;
		}

		for (int i = 0; i < threadCount; i++) {
			writerThread[i].join() ;
		}
		
		new ReaderThread(dir, 1).run() ;
	}
	
}

class ReaderThread implements Runnable {

	private Directory dir ;
	private int maxCount ;
	ReaderThread(Directory dir, int maxCount){
		this.dir = dir ;
		this.maxCount = maxCount ;
	}
	
	public void run() {
		IndexSearcher searcher = null ;
		try {
			Thread.sleep(100) ;
			searcher = new IndexSearcher(dir, true);
			for (int i = 0; i < maxCount; i++) {
				int docCount = searcher.getIndexReader().maxDoc() ;
				Debug.debug("Reader : " + docCount) ;
				Thread.sleep(70) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeQuietly(searcher) ;
		}
	}
}


class NewReaderThread implements Runnable {

	private Directory dir ;
	private int maxCount ;
	NewReaderThread(Directory dir, int maxCount){
		this.dir = dir ;
		this.maxCount = maxCount ;
	}
	
	public void run() {
		IndexSearcher searcher = null ;
		try {
			Thread.sleep(100) ;
			for (int i = 0; i < maxCount; i++) {
				searcher = new IndexSearcher(dir, true);
				int docCount = searcher.getIndexReader().maxDoc() ;
				Debug.debug("New Reader : " + docCount) ;
				IOUtil.closeQuietly(searcher) ;
				Thread.sleep(70) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeQuietly(searcher) ;
		}
	}
}


class SelfWriterThread implements Runnable {

	private int maxCount ;
	private Directory dir ;

	SelfWriterThread(Directory dir, int maxCount) throws CorruptIndexException, LockObtainFailedException, IOException{
		this.maxCount = maxCount ;
		this.dir = dir ;
	}
	
	public void run() {
		IndexWriter writer  = null ;
		try {
			WriteDocument[] docs = ISTestCase.makeTestDocument(maxCount) ;
			writer  = new IndexWriter(dir, new KoreanAnalyzer(), IndexWriter.MaxFieldLength.LIMITED); 
			for (WriteDocument doc : docs) {
				writer.addDocument(doc.toLuceneDoc()) ;
				try {
					Thread.sleep(50) ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Debug.debug(doc.get("NAME")) ;
				if (RandomUtil.nextBoolean()) writer.commit() ; 
				else writer.rollback() ;
			}
		} catch(IOException ex){
			ex.printStackTrace() ;
		} finally {
			IOUtil.closeQuietly(writer) ;
		}
	}
	

}




class OnlyWriterThread implements Runnable {

	private int maxCount ;
	private IndexWriter writer ;

	OnlyWriterThread(int maxCount, IndexWriter writer){
		this.maxCount = maxCount ;
		this.writer = writer ;
	}
	
	public void run() {
		try {
			WriteDocument[] docs = ISTestCase.makeTestDocument(maxCount) ;
			
			for (WriteDocument doc : docs) {
				writer.addDocument(doc.toLuceneDoc()) ;
				try {
					Thread.sleep(50) ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Debug.debug(doc.get("NAME")) ;
				if (RandomUtil.nextBoolean()) writer.commit() ; 
				else writer.rollback() ;
			}
		} catch(IOException ex){
			ex.printStackTrace() ;
		} finally {
			IOUtil.closeQuietly(writer) ;
		}
	}

}


class WriterThread implements Runnable {

	private Directory dir ;
	private int maxCount ;
	WriterThread(Directory dir, int maxCount){
		this.dir = dir ;
		this.maxCount = maxCount ;
	}
	
	public void run() {
		IndexWriter writer = null ;
		try {
			WriteDocument[] docs = ISTestCase.makeTestDocument(maxCount) ;
			
			for (WriteDocument doc : docs) {
				writer = new IndexWriter(dir, new KoreanAnalyzer(), IndexWriter.MaxFieldLength.LIMITED);
				writer.addDocument(doc.toLuceneDoc()) ;
				try {
					Thread.sleep(50) ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Debug.debug(doc.get("subject")) ;
				writer.commit() ;
				writer.close() ;
			}
		} catch(IOException ex){
			ex.printStackTrace() ;
		} finally {
			IOUtil.closeQuietly(writer) ;
		}
	}
	
}

