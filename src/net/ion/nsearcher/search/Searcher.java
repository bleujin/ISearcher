package net.ion.nsearcher.search;

import java.io.IOException;

import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.search.processor.PostProcessor;
import net.ion.nsearcher.search.processor.PreProcessor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;

public interface Searcher {

	public SearchConfig config() ;
	
	public SearchRequest createRequest(String query) throws ParseException  ;

	public SearchRequest createRequest(Query query)  ;

	public SearchRequest createRequest(Term term) ;
	
	public SearchRequest createRequest(String query, Analyzer analyzer) throws ParseException  ;
	
	public int totalCount(SearchRequest sreq) ; 

	public SearchResponse search(final SearchRequest sreq) throws IOException, ParseException ;
	
	public SearchResponse search(String query) throws IOException, ParseException ;

	public Searcher addPostListener(final PostProcessor processor) ;
	
	public Searcher addPreListener(final PreProcessor processor) ;

	public Searcher andFilter(Filter filter) ;

	public Searcher queryFilter(String query) throws ParseException;

	public SearchRequest createRequestByKey(String key) ;

	public SearchRequest createRequestByTerm(String tid, String value) ;

	public Analyzer queryAnalyzer() ;

	public IndexReader indexReader() throws IOException ;

}
