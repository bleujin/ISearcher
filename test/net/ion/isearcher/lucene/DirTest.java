package net.ion.isearcher.lucene;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.processor.StdOutProcessor;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
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
		
		Central cen1 = Central.createOrGet(writeDocument()) ;
		Central cen2 = write2Dir() ;
		
		IndexReader mreader = new MultiReader(new IndexReader[]{cen1.newReader().getIndexReader(), cen2.newReader().getIndexReader()}) ;
		long start = System.nanoTime() ;
		IndexSearcher searcher = new IndexSearcher(mreader) ;
		QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, IKeywordField.ISALL_FIELD, new CJKAnalyzer(Version.LUCENE_CURRENT)) ;
		
		Debug.debug(searcher.search(qp.parse("bleujin"), 10).totalHits) ; 
		Debug.debug(System.nanoTime() - start) ;
	}

	
	
	
	private Central write2Dir() throws LockObtainFailedException, IOException {
		Directory dir = new RAMDirectory() ;
		Central cen = Central.createOrGet(dir) ;
		IWriter writer = cen.testIndexer(new CJKAnalyzer(Version.LUCENE_CURRENT)) ;
		
		
		writer.begin("test") ;
		for (int i = 0; i < 3 ; i++) {
			MyDocument doc = MyDocument.testDocument() ;
			doc.add(MyField.text("name", "bleujin")) ;
			writer.insertDocument(doc) ;
		}
		writer.end() ;
		return cen ;
	}
	
	
	
	
}
