package net.ion.isearcher.searcher;

import junit.framework.TestCase;
import net.ion.framework.db.Page;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class FirstTest extends TestCase{


	public void testBlankSearcher() throws Exception {
		Directory dir = new RAMDirectory() ;

		Central c = Central.createOrGet(dir) ;
		ISearcher searcher = c.newSearcher() ;
		SearchResponse res = searcher.searchTest("");
		res.debugPrint(Page.ALL) ;
	}

}
