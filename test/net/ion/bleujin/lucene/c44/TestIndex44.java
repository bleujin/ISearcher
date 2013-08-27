package net.ion.bleujin.lucene.c44;

import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.AbDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import junit.framework.TestCase;

public class TestIndex44 extends TestCase {

	
	public void testLucene() throws Exception {
		Central cen = CentralConfig.newRam().build();
		
		Indexer indexer = cen.newIndexer();
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i : ListUtil.rangeNum(10)) {
					WriteDocument doc = isession.newDocument();
					doc.keyword("index", "" + i).text("text", "bleu jin") ;
					isession.insertDocument(doc) ;
				}
				return null;
			}
		}) ;
		
		
		Searcher searcher = cen.newSearcher();
		searcher.createRequest("").find().debugPrint() ;
		
	}
}
