package net.ion.nsearcher.index;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;

import org.apache.lucene.document.Document;

public interface FieldLoadable {

	WriteDocument handle(WriteDocument result, Document findDoc) throws IOException;

}
