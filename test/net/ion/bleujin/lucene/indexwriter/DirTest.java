package net.ion.bleujin.lucene.indexwriter;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class DirTest extends ISTestCase{

	
	public void testDiff() throws Exception {
		Directory dir1 = FSDirectory.open(getTestDirFile()) ;
		Directory dir2 = FSDirectory.open(getTestDirFile()) ;

		Debug.debug(dir1.getLockID().equals(dir2.getLockID()), dir1.getLockID(), dir1.getLockFactory(), dir1, dir2, dir1 == dir2, dir1.equals(dir2)) ;
	}
	
	public void testMaxOpen() throws Exception {
		for (int i = 0; i < 100000; i++) {
			Directory dir1 = FSDirectory.open(getTestDirFile()) ;
		}
	}
	
	
	public void testMultiReader() throws Exception {
		
		Central cen1 = writeDocument() ;
		Central cen2 = write2Dir() ;
		
		IndexReader mreader = new MultiReader(new IndexReader[]{cen1.newReader().getIndexReader(), cen2.newReader().getIndexReader()}) ;
		long start = System.nanoTime() ;
		IndexSearcher searcher = new IndexSearcher(mreader) ;
		QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, IKeywordField.ISALL_FIELD, new CJKAnalyzer(Version.LUCENE_CURRENT)) ;
		
		Debug.debug(searcher.search(qp.parse("bleujin"), 10).totalHits) ; 
		Debug.debug(System.nanoTime() - start) ;
	}

	
	
	
	private Central write2Dir() throws LockObtainFailedException, IOException {
		Central cen = CentralConfig.newRam().build() ;
		Indexer writer = cen.newIndexer() ;
		writer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				for (int i = 0; i < 3 ; i++) {
					WriteDocument doc = MyDocument.testDocument() ;
					doc.add(MyField.text("name", "bleujin")) ;
					session.insertDocument(doc) ;
				}
				return null;
			}
		}) ;
		
		return cen ;
	}
	
	
	
	
}
