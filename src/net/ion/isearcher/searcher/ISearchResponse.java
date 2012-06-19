package net.ion.isearcher.searcher;

import java.io.IOException;
import java.util.List;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.impl.ISearcher;

import org.apache.ecs.xml.XML;
import org.apache.lucene.index.CorruptIndexException;

public interface ISearchResponse {

	public int getTotalCount();

	// public List<MyDocument> getDocument(Page page) throws CorruptIndexException, IOException;
	public List<MyDocument> getDocument() throws CorruptIndexException, IOException;
	public IDocumentSet allDocumentSet() throws IOException ;
	// public QueryScorer getQueryScore() ;

	public ISearchRequest getRequest();
	public ISearcher getSearcher() ;
	
	public long getStartTime() ;
	public long elapsedTime() ;
	public XML toXML() ;
}
