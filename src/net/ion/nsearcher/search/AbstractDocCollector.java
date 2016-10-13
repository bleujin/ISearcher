package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;

import net.ion.nsearcher.common.ReadDocument;

public abstract class AbstractDocCollector implements DocCollector {
	public abstract ColResult accept(ReadDocument doc) ;
	
	public ColResult accept(DirectoryReader dreader, SearchRequest sreq, int docId) throws IOException{
		return accept(toDoc(dreader, sreq, docId)) ;
	}

	private ReadDocument toDoc(DirectoryReader dreader, SearchRequest sreq, int docId) throws IOException {
		Set<String> fields = sreq.selectorField();
		if (fields == null || fields.size() == 0) {
			return ReadDocument.loadDocument(docId, dreader.document(docId));
		}
		return ReadDocument.loadDocument(docId, dreader.document(docId, sreq.selectorField()));
	}

	
}
