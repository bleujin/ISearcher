package net.ion.nsearcher;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.SingleSearcher;
import net.ion.nsearcher.search.filter.FilterUtil;
import net.ion.nsearcher.search.processor.PostProcessor;
import net.ion.nsearcher.search.processor.PreProcessor;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;

public class Searcher {

	private Set<PostProcessor> postListeners = new HashSet<PostProcessor>();
	private Set<PreProcessor> preListeners = new HashSet<PreProcessor>();
	private SingleSearcher searcher ;
	
	public Searcher(SingleSearcher searcher) {
		this.searcher = searcher ;
	}

	public SearchResponse search(final SearchRequest sreq) throws IOException, ParseException {
		searcher.submit(new Callable<Void>(){
			public Void call() throws Exception {
				for (PreProcessor pre : preListeners) {
					pre.process() ;
				}
				return null;
			}
		}) ;
		
		final SearchResponse response = searcher.search(sreq, makeFilter(sreq));

		Future<Void> future = searcher.submit(new Callable<Void>(){
			public Void call() throws Exception {
				for(PostProcessor processor : postListeners) {
					processor.postNotify(sreq, response) ;
				}
				return null;
			}
		});
		
		response.postFuture(future) ;
		
		return response ;
	}
	
	private Filter makeFilter(final SearchRequest srequest) {
		if (myFilters.size() == 0 && srequest.getFilter() == null) return null ;
		
		Filter currentFilter = FilterUtil.and(myFilters.toArray(new Filter[0])) ;
		if (srequest.getFilter() != null) {
			currentFilter = FilterUtil.and(currentFilter, srequest.getFilter()) ;
		}
		
		return currentFilter;
	}
	
	
	public SearchResponse searchTest(String query) throws IOException, ParseException {
		return search(SearchRequest.create(query));
	}

	public final void addPostListener(final PostProcessor processor) {
		postListeners.add(processor) ;
	}
	
	public final void addPreListener(final PreProcessor processor) {
		preListeners.add(processor) ;
	}

	private Set<Filter> myFilters = new HashSet<Filter>();
	public Searcher andFilter(Filter filter) {
		if (filter == null) return this;
		myFilters.add(searcher.cachedFilter().getFilter(filter)) ;
		
		return this ;
	}

	// only test
	public void forceClose() {
		// TODO Auto-generated method stub
		
	}

}
