package net.ion.nsearcher.search;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;

public interface DocCollector {

	public enum ColResult {
		ACCEPT, REVOKE, BREAK
	}
	
	public final static DocCollector BLANK = new DocCollector() {
		public ColResult accept(DirectoryReader dreader, SearchRequest sreq, int docId) {
			return ColResult.ACCEPT;
		}
	};
	
	public ColResult accept(DirectoryReader dreader, SearchRequest sreq, int docId) throws IOException ;
}
