package net.ion.nsearcher.search;

import java.util.List;

public class TransformerKey {

	private ISearchable searcher;
	private List<Integer> docs;
	private SearchRequest sreq;

	public TransformerKey(ISearchable searcher, List<Integer> docs, SearchRequest sreq) {
		this.searcher = searcher;
		this.docs = docs;
		this.sreq = sreq;
	}

	public ISearchable searcher() {
		return searcher;
	}

	public List<Integer> docs() {
		return docs;
	}

	public SearchRequest request() {
		return sreq;
	}

}
