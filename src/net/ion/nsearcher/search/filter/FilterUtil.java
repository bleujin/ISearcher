package net.ion.nsearcher.search.filter;

import java.util.Collection;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilterClause;
import org.apache.lucene.search.BooleanClause.Occur;

public class FilterUtil {

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

}
