package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.concurrent.Future;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class TestMultiThreadIndexer extends TestCase {

	public void testIndexer() throws Exception {
		Directory dir = new RAMDirectory() ;
		Central c = Central.createOrGet(dir) ;

		Creater[] creaters = new Creater[10] ;
		for (int i : ListUtil.rangeNum(10)) {
			creaters[i] = new Creater(c, "n" + i + "m") ;
			creaters[i].start() ;
		}
		Thread.sleep(100) ;
		
		c.newDaemonHander().waitForFlushed() ;
		
		for (int i = 0; i < 10; i++) {
			Debug.line(c.newSearcher().searchTest("").getTotalCount()) ;
			Thread.sleep(500) ;
		}
	}
}



class Creater extends Thread {
	
	private Central central ;
	Creater(Central central, String name){
		super(name) ;
		this.central = central ; 
	}
	
	public void run(){
	
		int index = 0 ;
		int loop = 1000 ;
		
		while(loop-- > 0){
			try {
				Thread.sleep(RandomUtil.nextRandomInt(50, 100)) ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			DaemonIndexer di = central.newDaemonHander() ;
			final Analyzer myanal = new MyKoreanAnalyzer() ;
			
			Future<Integer> future = di.addIndexJob(new JobEntry<Integer>(){
				public Integer handle(IWriter writer) throws IOException {
					for (int i : ListUtil.rangeNum(10)) {
						MyDocument doc = MyDocument.testDocument() ;
						doc.add(MyField.number("index", i)) ;
						writer.insertDocument(doc) ;
					}
					return 10 ;
				}

				public Analyzer getAnalyzer() {
					return myanal ;
				}

				public void onException(Throwable ex) {
					ex.printStackTrace() ;
				}
			}) ;
			
			
			
			System.out.print('.') ;
		}
	}
}




