package net.ion.nsearcher.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

public class QueryUtil {

	public static Term createTerm(String name, int value) {
		BytesRef bytes = new BytesRef(NumericUtils.BUF_SIZE_INT);
		NumericUtils.intToPrefixCoded(value, 0, bytes);
		return new Term(name, bytes);
	}

	public static Term createTerm(String name, String value) {
		return new Term(name, value);
	}

	public static Term createTerm(String name, long value) {
		BytesRef bytes = new BytesRef(NumericUtils.BUF_SIZE_LONG);
		NumericUtils.longToPrefixCoded(value, 0, bytes);
		return new Term(name, bytes);
	}

}
