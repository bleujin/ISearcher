package net.ion.nsearcher.search;

import java.util.List;

public class TransformerKey {

	private SingleSearcher searcher;
	private List<Integer> docs;
	private SearchRequest sreq;

	public TransformerKey(SingleSearcher searcher, List<Integer> docs, SearchRequest sreq) {
		this.searcher = searcher;
		this.docs = docs;
		this.sreq = sreq;
	}

	public TSearcher searcher() {
		return searcher;
	}

	public List<Integer> docs() {
		return docs;
	}

	public SearchRequest request() {
		return sreq;
	}

}
