package net.ion.nsearcher.search.processor;

import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;


public interface PostProcessor {

	void postNotify(SearchRequest sreq, SearchResponse sres);
	
	
}
