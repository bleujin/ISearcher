package net.ion.nsearcher.search;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;

public class TestRequestSelection extends ISTestCase {

	
	private Central cen = null ; 
	public void setUp() throws Exception {
		super.setUp() ;
		cen = writeDocument() ;
	}
	
	public void testSelection() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		
		SearchResponse response = searcher.createRequest("").selections("name").lazySelections("int").find();
		
		List<ReadDocument> docs = response.getDocument();
		for (ReadDocument doc : docs) {
			Debug.debug(doc.get("name"), doc.get("int"), doc) ;
		}
		
	}
	
}
