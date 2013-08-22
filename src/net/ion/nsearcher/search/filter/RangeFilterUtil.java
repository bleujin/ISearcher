package net.ion.nsearcher.search.filter;

import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.util.BytesRef;

public class RangeFilterUtil {

	public final static TermRangeFilter termRangeFilter(String fieldName, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper){
		return new TermRangeFilter(fieldName, new BytesRef(lowerTerm), new BytesRef(upperTerm), includeLower, includeUpper) ;
	}
}
