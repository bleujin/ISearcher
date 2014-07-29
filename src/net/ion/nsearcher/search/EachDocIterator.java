package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.ion.nsearcher.common.ReadDocument;

import org.apache.lucene.search.TopDocs;

public class EachDocIterator implements Iterator<ReadDocument>{


	private final SingleSearcher searcher;
	private Iterator<Integer> docIter;
	private SearchRequest req;
	private int count;

	public EachDocIterator(SingleSearcher searcher, SearchRequest req, List<Integer> docIds) {
		this.searcher = searcher ;
		this.req = req ;
		this.count = docIds.size() ;
		this.docIter = docIds.iterator() ;
	}

	@Override
	public boolean hasNext() {
		return docIter.hasNext();
	}

	public int count(){
		return count ;
	}
	
	@Override
	public ReadDocument next() {
		try {
			return searcher.doc(docIter.next(), req);
		} catch (IOException e) {
			throw new IllegalStateException(e) ;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("readOnly") ;
	}

}
