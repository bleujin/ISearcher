package net.ion.nsearcher.search.processor;

import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;


public class SearchTask {

	private SearchResponse response ;
	private SearchRequest request ;
	public SearchTask(SearchRequest request, SearchResponse response) {
		this.request = request ;
		this.response = response ;
	}

	public SearchResponse getResult() {
		return response ;
	}
	
	public SearchRequest getRequest(){
		return request ;
	}

	public String toString(){
		return " Request : " + response.request() + " Result Count : " + response.getTotalCount() + " Elapsed Time(ms) : " + response.elapsedTime() ;
	}
}
