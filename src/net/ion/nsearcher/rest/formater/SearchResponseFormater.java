package net.ion.nsearcher.rest.formater;

import java.io.IOException;

import net.ion.nsearcher.search.SearchResponse;

import org.apache.lucene.index.CorruptIndexException;
import org.restlet.representation.Representation;

public interface SearchResponseFormater {
	Representation toRepresentation(SearchResponse iresult) throws CorruptIndexException, IOException ;
}
