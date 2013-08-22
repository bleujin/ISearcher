package net.ion.nsearcher.search.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.FilterClause;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;

public class BooleanFilter extends Filter implements Iterable<FilterClause> {

	private final List<FilterClause> clauses = new ArrayList<FilterClause>();

	/**
	 * Returns the a DocIdSetIterator representing the Boolean composition of the filters that have been added.
	 */
	@Override
	public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
		FixedBitSet res = null;
		final AtomicReader reader = context.reader();

		boolean hasShouldClauses = false;
		for (final FilterClause fc : clauses) {
			if (fc.getOccur() == Occur.SHOULD) {
				hasShouldClauses = true;
				final DocIdSetIterator disi = getDISI(fc.getFilter(), context);
				if (disi == null)
					continue;
				if (res == null) {
					res = new FixedBitSet(reader.maxDoc());
				}
				res.or(disi);
			}
		}
		if (hasShouldClauses && res == null)
			return null;

		for (final FilterClause fc : clauses) {
			if (fc.getOccur() == Occur.MUST_NOT) {
				if (res == null) {
					assert !hasShouldClauses;
					res = new FixedBitSet(reader.maxDoc());
					res.set(0, reader.maxDoc()); // NOTE: may set bits on deleted docs
				}
				final DocIdSetIterator disi = getDISI(fc.getFilter(), context);
				if (disi != null) {
					res.andNot(disi);
				}
			}
		}

		for (final FilterClause fc : clauses) {
			if (fc.getOccur() == Occur.MUST) {
				final DocIdSetIterator disi = getDISI(fc.getFilter(), context);
				if (disi == null) {
					return null; // no documents can match
				}
				if (res == null) {
					res = new FixedBitSet(reader.maxDoc());
					res.or(disi);
				} else {
					res.and(disi);
				}
			}
		}

		return BitsFilteredDocIdSet.wrap(res, acceptDocs);
	}

	private static DocIdSetIterator getDISI(Filter filter, AtomicReaderContext context) throws IOException {
		// we dont pass acceptDocs, we will filter at the end using an additional filter
		final DocIdSet set = filter.getDocIdSet(context, null);
		return set == null ? null : set.iterator();
	}

	public void add(FilterClause filterClause) {
		clauses.add(filterClause);
	}

	public final void add(Filter filter, Occur occur) {
		add(new FilterClause(filter, occur));
	}

	/**
	 * Returns the list of clauses
	 */
	public List<FilterClause> clauses() {
		return clauses;
	}

	public final Iterator<FilterClause> iterator() {
		return clauses().iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		final BooleanFilter other = (BooleanFilter) obj;
		return clauses.equals(other.clauses);
	}

	@Override
	public int hashCode() {
		return 657153718 ^ clauses.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder("BooleanFilter(");
		final int minLen = buffer.length();
		for (final FilterClause c : clauses) {
			if (buffer.length() > minLen) {
				buffer.append(' ');
			}
			buffer.append(c);
		}
		return buffer.append(')').toString();
	}
}