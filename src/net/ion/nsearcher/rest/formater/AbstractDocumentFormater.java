package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.util.List;

import net.ion.framework.rest.RopeRepresentation;
import net.ion.framework.rope.Rope;
import net.ion.nsearcher.common.MyDocument;

import org.apache.lucene.index.CorruptIndexException;
import org.restlet.data.CharacterSet;
import org.restlet.representation.Representation;

public abstract class AbstractDocumentFormater implements SearchDocumentFormater{
	
	public Representation toRepresentation(List<MyDocument> docs) throws CorruptIndexException, IOException{
		Rope rope = toRope(docs) ;
		Representation result =  new RopeRepresentation(rope, getMediaType());
		result.setCharacterSet(CharacterSet.UTF_8) ;
		return result ;
	}
}
