package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.search.filter.FilterUtil;
import net.ion.nsearcher.search.processor.PostProcessor;
import net.ion.nsearcher.search.processor.PreProcessor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;

public class SearcherImpl implements Searcher{

	private List<PostProcessor> postListeners = new ArrayList<PostProcessor>();
	private List<PreProcessor> preListeners = new ArrayList<PreProcessor>();
	private SingleSearcher searcher ;
	private SearchConfig sconfig;
	
	public SearcherImpl(SingleSearcher searcher, SearchConfig sconfig) {
		this.searcher = searcher ;
		this.sconfig = sconfig ;
	}
	
	public SearchConfig config(){
		return sconfig ;
	}
	
	public SearchRequest createRequest(String query) throws ParseException {
		return createRequest(query, sconfig.queryAnalyzer()) ;
	}

	public SearchRequest createRequest(Query query) {
		return new SearchRequest(this, query);
	}
	
	public SearchRequest createRequest(Term term) {
		return new SearchRequest(this, new TermQuery(term)) ;
	}


	public SearchRequest createRequest(String query, Analyzer analyzer) throws ParseException {
		if (StringUtil.isBlank(query)){
			return new SearchRequest(this, new MatchAllDocsQuery(), query) ;
		}
		
		final SearchRequest result = new SearchRequest(this, searcher.searchConfig().parseQuery(analyzer, query), query);
		return result;
	}
	
	public int totalCount(SearchRequest sreq) {
		return searcher.totalCount(sreq, makeFilter(sreq)) ;
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
	
	
	public SearchResponse search(String query) throws IOException, ParseException {
		return search(createRequest(query));
	}

	public final Searcher addPostListener(final PostProcessor processor) {
		postListeners.add(processor) ;
		return this ;
	}
	
	public final Searcher addPreListener(final PreProcessor processor) {
		preListeners.add(processor) ;
		return this ;
	}

	private Set<Filter> myFilters = new HashSet<Filter>();
	public Searcher andFilter(Filter filter) {
		if (filter == null) return this;
		myFilters.add(searcher.cachedFilter().getFilter(filter)) ;
		
		return this ;
	}
	
	public Searcher queryFilter(String query) throws ParseException{
		if (StringUtil.isBlank(query)) return this;
		return andFilter(new QueryWrapperFilter(sconfig.parseQuery(query))) ;
	}

	// only test
	public void forceClose() {
		IOUtil.closeQuietly(searcher) ;
	}

	public SearchRequest createRequestByKey(String key) {
		return this.createRequest(new TermQuery(new Term(IKeywordField.DocKey, key))) ;
	}

	public SearchRequest createRequestByTerm(String tid, String value) {
		return this.createRequest(new TermQuery(new Term(tid, value))) ;
	}

	public Analyzer queryAnalyzer() {
		return sconfig.queryAnalyzer() ;
	}

	public IndexReader indexReader() throws IOException{
		return searcher.indexReader() ;
	}
}
