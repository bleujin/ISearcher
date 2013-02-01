package net.ion.nsearcher.search.processor;

import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;


// test 
public class StdOutProcessor implements PostProcessor{

	private SearchTask task ;
	
	public void postNotify(SearchRequest sreq, SearchResponse sres) {
		this.task = new SearchTask(sreq, sres) ;
	}
	
	public int getTotalCount() {
		return task.getResult().totalCount() ;
	}
}
