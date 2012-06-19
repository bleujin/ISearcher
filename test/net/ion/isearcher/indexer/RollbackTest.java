package net.ion.isearcher.indexer;

import java.io.File;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.IReader;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.collect.FileCollector;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.store.Directory;

public class RollbackTest extends ISTestCase{
	
	public void testCollectorShutdown() throws Exception {
		
		Directory dir = writeDocument() ; // write 24
		Central central = Central.createOrGet(dir) ;
		
		assertEquals(24, central.newReader().numDoc());
		
		NonBlockingListener adapter = getAdapterListener(central.testIndexer(getAnalyzer())) ;
		
		FileCollector col = new FileCollector(new File("./"), true) ;
		col.addListener(adapter) ;
		Thread thread = new Thread(col) ;
		thread.start() ;
		
		Thread.sleep(100) ;
		col.shutdown("Die..") ;
		assertEquals(true, col.isShutDownState()) ;
		
		thread.join() ;
		adapter.joinIndexer() ;
		
		IReader reader = central.newReader() ;
	
		assertEquals(24, reader.numDoc()) ; // + file 3 + 3(CVS file)
		
		assertEquals(false, col.isShutDownState()) ;
	}
	
	
	public void testRollback() throws Exception {
		Directory dir = writeDocument() ;
		Central central = Central.createOrGet(dir) ;

		ISearcher searcher = central.newSearcher() ;
		
		searcher.searchTest("bleujin") ;
		IReader reader = central.newReader() ;
		
		MyDocument[] newDocs = makeTestMyDocument(10) ;
		IWriter writer = central.testIndexer(getAnalyzer()) ;
		
		writer.begin(this.getClass().getName()) ;
		for (MyDocument newdoc : newDocs) {
			newdoc.add(MyField.keyword("name", "bleuher")) ;
			writer.updateDocument(newdoc) ;
		}
		writer.rollback() ;
		writer.end() ;
		
		IReader newReader = central.newReader() ;
		assertEquals(24, reader.numDoc()) ;
		assertEquals(24, newReader.numDoc()) ; 
		
		
		IWriter newIndexr = central.testIndexer(getAnalyzer()) ;
		newIndexr.begin("test") ;
		newIndexr.updateDocument(newDocs[0]) ;
		newIndexr.rollback() ;
		newIndexr.end() ;
		
		central.destroySelf() ;
	}
	
	public void testReader() throws Exception {
		Directory dir = writeDocument() ;
		Central central = Central.createOrGet(dir) ;

		ISearcher searcher = central.newSearcher() ;
		
		searcher.searchTest("bleujin") ;
		IReader reader = central.newReader() ;
		
		MyDocument[] newDocs = makeTestMyDocument(10) ;
		IWriter writer = central.testIndexer(getAnalyzer()) ;
		
		writer.begin(this.getClass().getName()) ;
		for (MyDocument newdoc : newDocs) {
			newdoc.add(MyField.keyword("name", "bleuher")) ;
			writer.updateDocument(newdoc) ;
		}
		writer.end() ;
		
		IReader newReader = central.newReader() ;
		assertEquals(24, reader.numDoc()) ;
		assertEquals(38, newReader.numDoc()) ; 
	}
}
