package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.SearchResponse;
import net.ion.isearcher.searcher.filter.FilterUtil;
import net.ion.isearcher.searcher.processor.PostProcessor;
import net.ion.isearcher.searcher.processor.SearchTask;
import net.ion.isearcher.util.CloseUtils;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

public class ISearcher {

	private Central central ;
	private Set<Filter> filters = new HashSet<Filter>();
	private IndexSearcher searcher ;

	private Set<PostProcessor> postListeners = new HashSet<PostProcessor>();
	private Set<PreProcessor> preListeners = new HashSet<PreProcessor>();

	ISearcher(final Central central) throws IOException {
		this.central = central ;
	}

	public final void andFilter(final Filter filter){
		if (filter == null) return ;
		filters.add(central.getFilter(filter)) ;
		// filters.add(filter) ;
	}
	
	@Deprecated
	public final SearchResponse searchTest(final String query) throws ParseException, IOException {
		return searchTest(query, Page.HUNDRED); 
	}
	
	public SearchResponse searchTest(final String query, Page page) throws ParseException, IOException {
		return search(SearchRequest.test(query).setPage(page));
	}	
	
	public Set<Filter> confirmFilterSet(ISearchRequest srequest){
		Set<Filter> result = new HashSet<Filter>() ;
		for (Filter filter : filters) {
			result.add(central.getKeyFilter(filter)) ;
		}
		
		if (srequest.getFilter() != null) {
			result.add(srequest.getFilter()) ;
		}
		return result ;
	}
	
	public final SearchResponse search(final ISearchRequest srequest) throws ParseException, IOException {
		handlePreprocess() ;
		
		Query query =  srequest.getQuery() ;
		Sort sort = srequest.getSort() ;
		
		Filter currentFilter = makeFilter(srequest);
		if (currentFilter == null) currentFilter = new QueryWrapperFilter(query) ;

		long startTime = System.currentTimeMillis() ;
		TopDocs topDocs = getIndexSearcher().search(query, currentFilter, getLimitCount(srequest.getPage()), sort) ;
		SearchResponse result = makeSearchResult(srequest, topDocs, startTime);
		
		return result ; 
	}
	
	private int getLimitCount(Page page) {
		return page.getPageNo() * page.getListNum() + 1;
	}

	private void handlePreprocess() {
		for (PreProcessor pre : preListeners) {
			pre.process() ;
		}
	}

	public final SearchResponse searchLimit(final ISearchRequest srequest) throws ParseException, IOException {
		handlePreprocess() ;
		
		Filter currentFilter = makeFilter(srequest);
		Query query = srequest.getQuery();
		
		long startTime = System.currentTimeMillis() ;
		TopDocs topDocs = null ;
		if (currentFilter == null) {
			topDocs = getIndexSearcher().search(query, getLimitCount(srequest.getPage())) ;
		} else {
			topDocs = getIndexSearcher().search(query, currentFilter, getLimitCount(srequest.getPage())) ;
		}

		SearchResponse result = makeSearchResult(srequest, topDocs, startTime);
		return result ; 
	}

	private Filter makeFilter(final ISearchRequest srequest) {
		Filter currentFilter = FilterUtil.and(filters) ;
		if (srequest.getFilter() != null) {
			currentFilter = FilterUtil.and(currentFilter, srequest.getFilter()) ;
		}
		
		return currentFilter;
	}
	
	private SearchResponse makeSearchResult(final ISearchRequest srequest, final TopDocs docs, final long startTime) throws CorruptIndexException, IOException {
		SearchResponse result = SearchResponse.create(this, srequest, docs, startTime) ;
		
		for(PostProcessor processor : postListeners) {
			processor.postNotify(new SearchTask(srequest, result)) ;
		}
		return result ;
	}
	
	public final MyDocument doc(final int docId) throws IOException{
		return MyDocument.loadDocument(getIndexSearcher().doc(docId)) ;
	}
	
	public final Explanation getDocumentExplain(final ISearchRequest srequest, final int docId) throws IOException, ParseException {
		return getIndexSearcher().explain(srequest.getQuery(), docId);
	}

	
	// use only test..
	final void forceClose() throws IOException{
		// CloseUtils.silentClose(this.dir) ;
		CloseUtils.silentClose(getIndexSearcher()) ;
	}

	IndexSearcher getIndexSearcher() throws IOException {
		if (searcher == null) this.searcher = central.getIndexSearcher() ; 
		return searcher ;
//		return this.searcher = central.getIndexSearcher() ;
	}
	
	public void reopen() throws IOException{
		forceClose() ;
		this.searcher = central.getIndexSearcher() ;
	}

	public final boolean isModified(final ISearcher that) throws IOException{
		return this.getIndexSearcher() != that.getIndexSearcher() ;
	}
	
	final MyDocument[] allDocs() throws IOException {
		List<MyDocument> list = ListUtil.newList() ;
		IndexReader indexReader = getIndexSearcher().getIndexReader();
		for (int i = 0, max = indexReader.maxDoc(); i < max; i++) {
			MyDocument doc = MyDocument.loadDocument(indexReader.document(i)) ;
			list.add(doc) ;
		}
		return list.toArray(new MyDocument[0]);
	}

	public final void addPostListener(final PostProcessor processor) {
		postListeners.add(processor) ;
	}
	public final void addPreListener(final PreProcessor processor) {
		preListeners.add(processor) ;
	}

}
