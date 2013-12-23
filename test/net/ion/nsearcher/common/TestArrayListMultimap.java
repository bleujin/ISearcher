package net.ion.nsearcher.common;

import java.io.File;

import junit.framework.TestCase;
import net.ion.framework.util.FileUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

public class TestArrayListMultimap extends TestCase {

	public void testSpeed() throws Exception {
		FileUtil.deleteDirectory(new File("./resource/findex")) ;
		
		Central central = CentralConfig.newLocalFile().dirFile("./resource/findex").build();
		Indexer indexer = central.newIndexer();
		
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.setIgnoreBody(true) ;
				for (int i = 0; i < 20000; i++) {
					WriteDocument doc = isession.newDocument("/bleujin/" + i).keyword("name", "bleujin").number("age", 20).text("text", "thinking is high");
					isession.updateDocument(doc) ;
				}
				return null;
			}
		}) ;
		
		Searcher searcher = central.newSearcher();
		searcher.createRequestByKey("/bleujin/333").find().debugPrint() ;
	}
	
}
