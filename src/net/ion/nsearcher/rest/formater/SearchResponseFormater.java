package net.ion.nsearcher.rest.formater;

import java.io.IOException;

import javax.ws.rs.core.StreamingOutput;

import net.ion.nsearcher.search.SearchResponse;

import org.apache.lucene.index.CorruptIndexException;

public interface SearchResponseFormater {
	StreamingOutput outputStreaming(SearchResponse iresult) throws CorruptIndexException, IOException ;
}
