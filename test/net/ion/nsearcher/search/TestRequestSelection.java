package net.ion.nsearcher.search;

import java.util.List;

import org.apache.lucene.search.Filter;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.search.filter.TermFilter;

public class TestRequestSelection extends ISTestCase {

	
	private Central cen = null ; 
	public void setUp() throws Exception {
		super.setUp() ;
		cen = writeDocument() ;
	}
	
	public void testSelection() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		
		SearchResponse response = searcher.createRequest("").selections("name").lazySelections("int").find();
		
		List<MyDocument> docs = response.getDocument();
		for (MyDocument doc : docs) {
			Debug.debug(doc.get("name"), doc.get("int"), doc) ;
		}
		
	}
	
}
