package net.ion.isearcher.searcher.filter;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.BooleanClause.Occur;

public class FilterClause implements java.io.Serializable {
	private static final long serialVersionUID = 617413802136463705L;
	private Occur occur = null;
	private Filter filter = null;

	public FilterClause(Filter filter, Occur occur) {
		this.occur = occur;
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	public Occur getOccur() {
		return occur;
	}

}
