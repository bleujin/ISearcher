package net.ion.isearcher.rest.formater;

import java.io.IOException;

import net.ion.isearcher.searcher.ISearchResponse;

import org.apache.lucene.index.CorruptIndexException;
import org.restlet.representation.Representation;

public interface SearchResponseFormater {
	Representation toRepresentation(ISearchResponse iresult) throws CorruptIndexException, IOException ;
}
