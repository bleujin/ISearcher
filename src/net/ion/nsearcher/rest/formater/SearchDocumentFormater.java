package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.util.List;

import net.ion.framework.rope.Rope;
import net.ion.nsearcher.common.MyDocument;

import org.apache.lucene.index.CorruptIndexException;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public interface SearchDocumentFormater {
	Rope toRope(List<MyDocument> docs) throws IOException ;
	MediaType getMediaType() ;
	Representation toRepresentation(List<MyDocument> docs) throws CorruptIndexException, IOException ;
	
}
