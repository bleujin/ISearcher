package net.ion.nsearcher.index;

import java.io.File;
import java.io.IOException;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.index.collect.FileCollector;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.search.Searcher;

public class TestRollback extends ISTestCase{
	
	public void testCollectorShutdown() throws Exception {
		
		Central central = writeDocument() ; // write 24
		
		assertEquals(24, central.newReader().numDoc());
		
		NonBlockingListener adapter = getNonBlockingListener(central.newIndexer()) ;
		
		FileCollector col = new FileCollector(new File("./"), true) ;
		col.addListener(adapter) ;
		Thread thread = new Thread(col) ;
		thread.start() ;
		
		Thread.sleep(50) ;
		col.shutdown("Die..") ;
		assertEquals(true, col.isShutDownState()) ;
		
		thread.join() ;
		adapter.waitForCompleted() ;
		
		InfoReader reader = central.newReader() ;
	
		assertEquals(24, reader.numDoc()) ; // + file 3 + 3(CVS file)
		
		assertEquals(false, col.isShutDownState()) ;
	}
	
	
	public void testRollback() throws Exception {
		Central central = writeDocument() ;

		Searcher searcher = central.newSearcher() ;
		
		searcher.search("bleujin") ;
		InfoReader reader = central.newReader() ;
		
		final MyDocument[] newDocs = makeTestMyDocument(10) ;
		
		Indexer indexer = central.newIndexer();
		indexer.index(createDefaultAnalyzer(), new IndexJob<Void>() {
			public Void handle(IndexSession session) throws IOException {
				for (MyDocument newdoc : newDocs) {
					newdoc.add(MyField.keyword("name", "bleuher")) ;
					session.updateDocument(newdoc) ;
				}
				session.rollback() ;
				return null;
			}
		}) ;
		
		
		InfoReader newReader = central.newReader() ;
		assertEquals(24, reader.numDoc()) ;
		assertEquals(24, newReader.numDoc()) ; 
		
		
		Indexer newIndexer = central.newIndexer();
		newIndexer.index(createDefaultAnalyzer(), new IndexJob<Void>() {
			public Void handle(IndexSession session) throws IOException {
				session.updateDocument(newDocs[0]) ;
				session.rollback() ;
				return null;
			}
		}) ;
		
		central.destroySelf() ;
	}
	
	public void testReader() throws Exception {
		Central central = writeDocument() ;
		Searcher searcher = central.newSearcher() ;
		searcher.search("bleujin") ;
		
		InfoReader oldReader = central.newReader() ;
		Indexer indexer = central.newIndexer() ;
		indexer.index(createDefaultAnalyzer(), new IndexJob<Void>() {
			public Void handle(IndexSession session) throws IOException {
				MyDocument[] newDocs = makeTestMyDocument(10) ;
				for (MyDocument newdoc : newDocs) {
					newdoc.add(MyField.keyword("name", "bleuher")) ;
					session.updateDocument(newdoc) ;
				}
				return null;
			}
		}) ;
		
		assertEquals(38, oldReader.numDoc()) ;
		
		InfoReader newReader = central.newReader() ;
		assertEquals(38, newReader.numDoc()) ; 
	}
}
