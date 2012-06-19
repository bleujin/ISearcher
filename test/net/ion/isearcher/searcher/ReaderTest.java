package net.ion.isearcher.searcher;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.IReader;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.store.Directory;

public class ReaderTest extends ISTestCase {

	public void testCommit() throws Exception {
		Directory dir = writeDocument() ;
		Central central = Central.createOrGet(dir) ;
		
		IWriter indexer = central.newIndexer(getAnalyzer()) ;
		indexer.begin("my") ;
		indexer.end() ;

		IReader reader = central.newReader() ;
		for (IndexCommit commit: reader.listCommits()) {
			Debug.debug(commit.getDirectory(), commit.getFileNames(), commit.getSegmentsFileName(), commit.getUserData()) ;
			Debug.debug(commit.isDeleted(), commit.getVersion(), commit.getGeneration()) ;
		}
		
		// central.testIndexer(getAnalyzer()).end() ;
	}
}
