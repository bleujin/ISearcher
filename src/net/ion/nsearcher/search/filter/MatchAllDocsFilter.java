package net.ion.nsearcher.search.filter;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;

public class MatchAllDocsFilter extends Filter {

	private static final long serialVersionUID = -2456523434808692130L;
	public final static MatchAllDocsFilter SELF = new MatchAllDocsFilter() ;
	
	@Override
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		return new AllDocSet(reader.maxDoc());
	}

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
}

abstract class DocSet extends DocIdSet {

	public static final DocSet EMPTY_DOC_SET = new DocSet() {
		@Override
		public boolean get(int doc) throws IOException {
			return false;
		}

		@Override
		public DocIdSetIterator iterator() throws IOException {
			return DocIdSet.EMPTY_DOCIDSET.iterator();
		}

		@Override
		public boolean isCacheable() {
			return true;
		}

		@Override
		public long sizeInBytes() {
			return 0;
		}
	};

	public abstract boolean get(int doc) throws IOException;

	public abstract long sizeInBytes();
}

class RamUsage {

	private static final String OS_ARCH = System.getProperty("os.arch");
	private static final boolean JRE_IS_64BIT;

	static {
		String x = System.getProperty("sun.arch.data.model");
		if (x != null) {
			JRE_IS_64BIT = x.indexOf("64") != -1;
		} else {
			if (OS_ARCH != null && OS_ARCH.indexOf("64") != -1) {
				JRE_IS_64BIT = true;
			} else {
				JRE_IS_64BIT = false;
			}
		}
	}

	public final static int NUM_BYTES_SHORT = 2;
	public final static int NUM_BYTES_INT = 4;
	public final static int NUM_BYTES_LONG = 8;
	public final static int NUM_BYTES_FLOAT = 4;
	public final static int NUM_BYTES_DOUBLE = 8;
	public final static int NUM_BYTES_CHAR = 2;
	public final static int NUM_BYTES_OBJECT_HEADER = 8;
	public final static int NUM_BYTES_OBJECT_REF = JRE_IS_64BIT ? 8 : 4;
	public final static int NUM_BYTES_ARRAY_HEADER = NUM_BYTES_OBJECT_HEADER + NUM_BYTES_INT + NUM_BYTES_OBJECT_REF;

}

class AllDocSet extends DocSet {

	private final int maxDoc;

	public AllDocSet(int maxDoc) {
		this.maxDoc = maxDoc;
	}

	@Override
	public boolean isCacheable() {
		return true;
	}

	@Override
	public boolean get(int doc) throws IOException {
		return doc < maxDoc;
	}

	@Override
	public long sizeInBytes() {
		return RamUsage.NUM_BYTES_INT;
	}

	@Override
	public DocIdSetIterator iterator() throws IOException {
		return new AllDocIdSetIterator(maxDoc);
	}

	public static final class AllDocIdSetIterator extends DocIdSetIterator {

		private final int maxDoc;

		private int doc = -1;

		public AllDocIdSetIterator(int maxDoc) {
			this.maxDoc = maxDoc;
		}

		@Override
		public int docID() {
			return doc;
		}

		@Override
		public int nextDoc() throws IOException {
			if (++doc < maxDoc) {
				return doc;
			}
			return doc = NO_MORE_DOCS;
		}

		@Override
		public int advance(int target) throws IOException {
			doc = target;
			if (doc < maxDoc) {
				return doc;
			}
			return doc = NO_MORE_DOCS;
		}
	}
}
