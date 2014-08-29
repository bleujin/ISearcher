package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import net.ion.framework.rope.Rope;
import net.ion.nsearcher.common.ReadDocument;

import org.apache.lucene.index.CorruptIndexException;

public interface SearchDocumentFormater {
	StreamingOutput outputStreaming(List<ReadDocument> docs) throws CorruptIndexException, IOException ;
	
}
