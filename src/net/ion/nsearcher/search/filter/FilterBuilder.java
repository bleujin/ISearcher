package net.ion.nsearcher.search.filter;

import java.util.Set;

import net.ion.framework.util.SetUtil;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.util.BytesRef;

public class FilterBuilder {


	private Set<Filter> filters = SetUtil.newOrdereddSet();

	public FilterBuilder term(String fname, String... values){
		return add(FilterUtil.term(fname, values)) ;
	}


	public FilterBuilder termNot(String fname, String... values){
		return add(FilterUtil.not(FilterUtil.term(fname, values))) ;
	}


	public FilterBuilder query(String query, Searcher searcher) throws ParseException{
		return add(FilterUtil.query(query, searcher)) ;
	}
	
	
	private FilterBuilder add(Filter filter) {
		filters.add(filter) ;
		return this ;
	}


	public FilterBuilder filter(Filter filter){
		add(filter) ;
		return this ;
	}
	
	public FilterBuilder between(String fname, String lowerTerm, String upperTerm){
		return add(FilterUtil.between(fname, lowerTerm, upperTerm)) ;
	}
	
	public FilterBuilder gt(String fname, String lowerTerm){
		return add(FilterUtil.gt(fname, lowerTerm)) ;
	}

	public FilterBuilder gte(String fname, String lowerTerm){
		return add(FilterUtil.gte(fname, lowerTerm)) ;
	}

	public FilterBuilder lt(String fname, String upperTerm){
		return add(FilterUtil.lt(fname, upperTerm)) ;
	}

	public FilterBuilder lte(String fname, String upperTerm){
		return add(FilterUtil.lte(fname, upperTerm)) ;
	}

	public FilterBuilder between(String fname, long lowerTerm, long upperTerm){
		return add(FilterUtil.between(fname, lowerTerm, upperTerm)) ;
	}
	
	public FilterBuilder gt(String fname, long lowerTerm){
		return add(FilterUtil.gt(fname, lowerTerm)) ;
	}

	public FilterBuilder gte(String fname, long lowerTerm){
		return add(FilterUtil.gte(fname, lowerTerm)) ;
	}

	public FilterBuilder lt(String fname, long upperTerm){
		return add(FilterUtil.lt(fname, upperTerm)) ;
	}

	public FilterBuilder lte(String fname, long upperTerm){
		return add(FilterUtil.lte(fname, upperTerm)) ;
	}

	public Filter andBuild() {
		return FilterUtil.and(filters) ;
	}

	public Filter orBuild() {
		return FilterUtil.or(filters) ;
	}


}
