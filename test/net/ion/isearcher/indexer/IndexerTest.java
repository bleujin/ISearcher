package net.ion.isearcher.indexer;

import java.io.File;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.collect.FileCollector;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.ISearchResponse;
import net.ion.isearcher.searcher.processor.StdOutProcessor;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class IndexerTest extends ISTestCase {

	File file = getTestDir("/ion_page");

	public void testCreate() throws Exception {
		Directory dir = writeDocument() ;
		Central central = Central.createOrGet(dir) ;
		
		FileCollector col = new FileCollector(file, true);
		IWriter writer = central.newIndexer(getAnalyzer()) ;

		NonBlockingListener adapterListener = getAdapterListener(writer);
		col.addListener(adapterListener) ;
		// col.addListener(new DefaultReportor()) ;
		
		col.collect() ;
		adapterListener.joinIndexer() ;
		central.destroySelf() ;
	}

	public void testAfterClose() throws Exception {
		Directory dir = writeDocument() ;
		Central c = Central.createOrGet(dir);
		ISearcher s1 = c.newSearcher() ;
		s1.addPostListener(new StdOutProcessor()) ;
		IWriter writer = c.testIndexer(new StandardAnalyzer(Version.LUCENE_CURRENT)) ;
		writer.begin("test") ;
		writer.end() ;
		c.newSearcher() ; // new Searcher..
		Thread.sleep(1000) ;
		ISearchResponse sr = s1.searchTest("bleujin") ;
		
		List<MyDocument> docs = sr.getDocument() ;
		Debug.debug(docs) ;
		
	}



}
