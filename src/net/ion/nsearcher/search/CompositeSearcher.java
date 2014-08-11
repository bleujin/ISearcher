package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.search.filter.FilterUtil;
import net.ion.nsearcher.search.processor.PostProcessor;
import net.ion.nsearcher.search.processor.PreProcessor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class CompositeSearcher implements Searcher {

	private Central main;
	private SearchConfig sconfig;
	private Set<PostProcessor> postListeners = new HashSet<PostProcessor>();
	private Set<PreProcessor> preListeners = new HashSet<PreProcessor>();
	private MultiSearcher searcher;

	public CompositeSearcher(Central main, List<Central> others) {
		this.main = main;
		this.searcher = new MultiSearcher(main, others) ;
		this.sconfig = main.searchConfig();
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


	public SearchRequest createRequest(String query, Analyzer analyzer) throws ParseException {
		if (StringUtil.isBlank(query)){
			return new SearchRequest(this, new MatchAllDocsQuery()) ;
		}
		
		final SearchRequest result = new SearchRequest(this, sconfig.parseQuery(analyzer, query));
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

	public final void addPostListener(final PostProcessor processor) {
		postListeners.add(processor) ;
	}
	
	public final void addPreListener(final PreProcessor processor) {
		preListeners.add(processor) ;
	}

	private Set<Filter> myFilters = new HashSet<Filter>();
	public Searcher andFilter(Filter filter) {
		myFilters.add(filter) ;
		
		return this ;
	}
	public SearchRequest createRequestByKey(String key) {
		return this.createRequest(new TermQuery(new Term(IKeywordField.ISKey, key))) ;
	}

	public SearchRequest createRequestByTerm(String tid, String value) {
		return this.createRequest(new TermQuery(new Term(tid, value))) ;
	}

	public Analyzer queryAnalyzer() {
		return sconfig.queryAnalyzer() ;
	}

}

class MultiSearcher implements ISearchable{

	private ExecutorService es;
	private List<Central> cs;

	public MultiSearcher(Central main, List<Central> others) {
		this.es = main.searchConfig().executorService() ;
		this.cs = ListUtil.newList() ;
		cs.add(main) ;
		for (Central other : others) {
			cs.add(other) ;
		}
	}
	
	public SearchResponse search(SearchRequest sreq, Filter filters) throws IOException {
		long startTime = System.currentTimeMillis();
		TopDocs docs = createIndexSearcher().search(sreq.query(), filters, sreq.limit(), sreq.sort());
		return SearchResponse.create(this, sreq, docs, startTime);
	}

	public int totalCount(SearchRequest sreq, Filter filters) {
		try {
			TopDocs docs = createIndexSearcher().search(sreq.query(), filters, Integer.MAX_VALUE);
			return docs.totalHits;
		} catch (IOException e) {
			return -1 ;
		}
	}

	public <T> Future<T> submit(Callable<T> task) {
		return es.submit(task);
	}

	// Once you have a new IndexReader, it's relatively cheap to create a new IndexSearcher from it. 
	private IndexSearcher createIndexSearcher() throws IOException {
		return new IndexSearcher(indexReader());
	}

	@Override
	public ReadDocument doc(int docId, SearchRequest request) throws IOException {
		Set<String> fields = request.selectorField();
		if (fields == null || fields.size() == 0) {
			return ReadDocument.loadDocument(indexReader().document(docId));
		}
		return ReadDocument.loadDocument(indexReader().document(docId, request.selectorField()));
	}

	private IndexReader indexReader() throws IOException{
		IndexReader[] ireaders = new IndexReader[cs.size()] ;
		int i = 0 ;
		for (Central c : cs) {
			ireaders[i++] = c.newReader().getIndexReader() ;
		}
		return new MultiReader(ireaders) ;
	}
	
	@Override
	public InfoReader reader() {
		throw new UnsupportedOperationException("this is composite searcher : ") ;
	}


}
