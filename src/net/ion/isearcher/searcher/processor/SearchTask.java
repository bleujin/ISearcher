package net.ion.isearcher.searcher.processor;

import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.ISearchResponse;

public class SearchTask {

	private ISearchResponse response ;
	private ISearchRequest request ;
	public SearchTask(ISearchRequest request, ISearchResponse response) {
		this.request = request ;
		this.response = response ;
	}

	public ISearchResponse getResult() {
		return response ;
	}
	
	public ISearchRequest getRequest(){
		return request ;
	}

	public String toString(){
		return " Request : " + response.getRequest() + " Result Count : " + response.getTotalCount() + " Elapsed Time(ms) : " + response.elapsedTime() ;
	}
}
