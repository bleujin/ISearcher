package net.ion.nsearcher.search.filter;

import java.util.Collection;
import java.util.Set;

import net.ion.framework.util.SetUtil;

import org.apache.lucene.index.Term;
import org.apache.lucene.queries.FilterClause;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.NumericUtils;

public class FilterUtil {

	public static FilterBuilder newBuilder(){
		return new FilterBuilder() ;
	} 
	
	public final static Filter and(Filter filter1, Filter filter2) {
		return and(new Filter[] { filter1, filter2 });
	}

	public final static Filter and(Filter[] filters) {
		if (filters == null || filters.length == 0) return null ;

		BooleanFilter result = new BooleanFilter();
		int filterCount = 0 ;
		for (Filter filter : filters) {
			if (filter == null) continue ;
			filterCount++ ;
			result.add(new FilterClause(filter, Occur.MUST));
		}
		if (filterCount < 1) return null ;
		return result;
	}


	public final static Filter or(Filter[] filters) {
		if (filters == null || filters.length == 0) return null ;

		BooleanFilter result = new BooleanFilter();
		int filterCount = 0 ;
		for (Filter filter : filters) {
			if (filter == null) continue ;
			filterCount++ ;
			result.add(new FilterClause(filter, Occur.SHOULD));
		}
		if (filterCount < 1) return null ;
		return result;
	}

	public static Filter and(Collection<Filter> filters) {
		return and (filters.toArray(new Filter[0]));
	}

	public static Filter or(Set<Filter> filters) {
		return or (filters.toArray(new Filter[0]));
	}

	
	public static Filter term(String fname, String... values){
		Set<Term> set = SetUtil.newSet() ;
		for (String value : values) {
			set.add(new Term(fname, value)) ;
		}
		return new TermsFilter(set.toArray(new Term[0])) ;
	}

	
	public static TermRangeFilter between(String fname, String lowerTerm, String upperTerm){
		return new TermRangeFilter(fname, new BytesRef(lowerTerm), new BytesRef(upperTerm), true, true) ;
	}
	
	public static TermRangeFilter gt(String fname, String lowerTerm){
		return new TermRangeFilter(fname, new BytesRef(lowerTerm), null, false, true) ;
	}

	public static TermRangeFilter gte(String fname, String lowerTerm){
		return new TermRangeFilter(fname, new BytesRef(lowerTerm), null, true, true) ;
	}

	public static TermRangeFilter lt(String fname, String upperTerm){
		return new TermRangeFilter(fname, null, new BytesRef(upperTerm), true, false) ;
	}

	public static TermRangeFilter lte(String fname, String upperTerm){
		return new TermRangeFilter(fname, null, new BytesRef(upperTerm), true, true) ;
	}


	public static Filter between(String fname, long lowerTerm, long upperTerm){
		return NumericRangeFilter.newLongRange(fname, lowerTerm, upperTerm, true, true) ;
	}
	
	public static Filter gt(String fname, long lowerTerm){
		return NumericRangeFilter.newLongRange(fname, lowerTerm, Long.MAX_VALUE, false, true) ;
	}

	public static Filter gte(String fname, long lowerTerm){
		return NumericRangeFilter.newLongRange(fname, lowerTerm, Long.MAX_VALUE, true, true) ;
	}

	public static Filter lt(String fname, long upperTerm){
		return NumericRangeFilter.newLongRange(fname, Long.MIN_VALUE, upperTerm, true, false) ;
	}

	public static Filter lte(String fname, long upperTerm){
		return NumericRangeFilter.newLongRange(fname, Long.MIN_VALUE, upperTerm, true, true) ;
	}


	
	
	
	

	
}
