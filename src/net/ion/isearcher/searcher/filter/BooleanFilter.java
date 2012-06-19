package net.ion.isearcher.searcher.filter;

import java.io.IOException;
import java.util.List;

import net.ion.framework.util.ListUtil;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.OpenBitSet;
import org.apache.lucene.util.OpenBitSetDISI;
import org.apache.lucene.util.SortedVIntList;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.OpenBitSet;
import org.apache.lucene.util.OpenBitSetDISI;


public class BooleanFilter extends Filter {
	ArrayList<Filter> shouldFilters = null;
	ArrayList<Filter> notFilters = null;
	ArrayList<Filter> mustFilters = null;

	private DocIdSetIterator getDISI(ArrayList<Filter> filters, int index, IndexReader reader) throws IOException {
		return filters.get(index).getDocIdSet(reader).iterator();
	}

	/**
	 * Returns the a DocIdSetIterator representing the Boolean composition of the filters that have been added.
	 */
	@Override
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		OpenBitSetDISI res = null;

		if (shouldFilters != null) {
			for (int i = 0; i < shouldFilters.size(); i++) {
				if (res == null) {
					res = new OpenBitSetDISI(getDISI(shouldFilters, i, reader), reader.maxDoc());
				} else {
					DocIdSet dis = shouldFilters.get(i).getDocIdSet(reader);
					if (dis instanceof OpenBitSet) {
						// optimized case for OpenBitSets
						res.or((OpenBitSet) dis);
					} else {
						res.inPlaceOr(getDISI(shouldFilters, i, reader));
					}
				}
			}
		}

		if (notFilters != null) {
			for (int i = 0; i < notFilters.size(); i++) {
				if (res == null) {
					res = new OpenBitSetDISI(getDISI(notFilters, i, reader), reader.maxDoc());
					res.flip(0, reader.maxDoc()); // NOTE: may set bits on deleted docs
				} else {
					DocIdSet dis = notFilters.get(i).getDocIdSet(reader);
					if (dis instanceof OpenBitSet) {
						// optimized case for OpenBitSets
						res.andNot((OpenBitSet) dis);
					} else {
						res.inPlaceNot(getDISI(notFilters, i, reader));
					}
				}
			}
		}

		if (mustFilters != null) {
			for (int i = 0; i < mustFilters.size(); i++) {
				if (res == null) {
					res = new OpenBitSetDISI(getDISI(mustFilters, i, reader), reader.maxDoc());
				} else {
					DocIdSet dis = mustFilters.get(i).getDocIdSet(reader);
					if (dis instanceof OpenBitSet) {
						// optimized case for OpenBitSets
						res.and((OpenBitSet) dis);
					} else {
						res.inPlaceAnd(getDISI(mustFilters, i, reader));
					}
				}
			}
		}

		if (res != null)
			return finalResult(res, reader.maxDoc());

		return DocIdSet.EMPTY_DOCIDSET;
	}

	/**
	 * Provide a SortedVIntList when it is definitely smaller than an OpenBitSet.
	 * 
	 * @deprecated Either use CachingWrapperFilter, or switch to a different DocIdSet implementation yourself. This method will be removed in Lucene 4.0
	 */
	protected final DocIdSet finalResult(OpenBitSetDISI result, int maxDocs) {
		return result;
	}

	/**
	 * Adds a new FilterClause to the Boolean Filter container
	 * 
	 * @param filterClause
	 *            A FilterClause object containing a Filter and an Occur parameter
	 */

	public void add(FilterClause filterClause) {
		if (filterClause.getOccur().equals(Occur.MUST)) {
			if (mustFilters == null) {
				mustFilters = new ArrayList<Filter>();
			}
			mustFilters.add(filterClause.getFilter());
		}
		if (filterClause.getOccur().equals(Occur.SHOULD)) {
			if (shouldFilters == null) {
				shouldFilters = new ArrayList<Filter>();
			}
			shouldFilters.add(filterClause.getFilter());
		}
		if (filterClause.getOccur().equals(Occur.MUST_NOT)) {
			if (notFilters == null) {
				notFilters = new ArrayList<Filter>();
			}
			notFilters.add(filterClause.getFilter());
		}
	}

	private boolean equalFilters(ArrayList<Filter> filters1, ArrayList<Filter> filters2) {
		return (filters1 == filters2) || ((filters1 != null) && filters1.equals(filters2));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		BooleanFilter other = (BooleanFilter) obj;
		return equalFilters(notFilters, other.notFilters) && equalFilters(mustFilters, other.mustFilters) && equalFilters(shouldFilters, other.shouldFilters);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == mustFilters ? 0 : mustFilters.hashCode());
		hash = 31 * hash + (null == notFilters ? 0 : notFilters.hashCode());
		hash = 31 * hash + (null == shouldFilters ? 0 : shouldFilters.hashCode());
		return hash;
	}

	/** Prints a user-readable version of this query. */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("BooleanFilter(");
		appendFilters(shouldFilters, "", buffer);
		appendFilters(mustFilters, "+", buffer);
		appendFilters(notFilters, "-", buffer);
		buffer.append(")");
		return buffer.toString();
	}

	private void appendFilters(ArrayList<Filter> filters, String occurString, StringBuilder buffer) {
		if (filters != null) {
			for (int i = 0; i < filters.size(); i++) {
				buffer.append(' ');
				buffer.append(occurString);
				buffer.append(filters.get(i).toString());
			}
		}
	}
}
