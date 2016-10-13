package net.ion.nsearcher.search;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;

import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.reader.InfoReader;

public interface ISearchable {

	public ReadDocument doc(int docId, SearchRequest request) throws IOException ;
	public InfoReader reader()  ;
	public int totalCount(SearchRequest sreq, Filter filters) ;
	public IndexReader indexReader() throws IOException ;
	public SearchConfig searchConfig() ;
}
