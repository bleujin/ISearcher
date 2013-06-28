package net.ion.nsearcher.search;

import java.io.IOException;

import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.reader.InfoReader;

public interface TSearcher {

	public ReadDocument doc(int docId, SearchRequest request) throws IOException ;
	public InfoReader reader()  ;

}
