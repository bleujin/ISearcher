package net.ion.nsearcher.search.filter;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.util.Bits;

public class MatchAllDocsFilter extends Filter {

	private static final long serialVersionUID = -2456523434808692130L;
	public final static Filter SELF = new QueryWrapperFilter(new MatchAllDocsQuery()) ;

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null) {
			return false;
		}

		if (obj.getClass() == this.getClass()) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return "*:*";
	}

	@Override
	public DocIdSet getDocIdSet(AtomicReaderContext context, Bits bits) throws IOException {
		return SELF.getDocIdSet(context, bits);
	}
}
