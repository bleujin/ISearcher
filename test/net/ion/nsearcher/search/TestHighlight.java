package net.ion.nsearcher.search;

import java.io.IOException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.DocHighlighter;
import net.ion.nsearcher.search.EachDocHandler;
import net.ion.nsearcher.search.EachDocIterator;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

public class TestHighlight extends TestCase {

	public void testHighlight() throws Exception {
		Central cen = CentralConfig.newRam().build();

		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().add(MyField.text("tv", "The highlight package contains classes to provide keyword in context features typically used to highlight search terms in the text of results pages. The Highlight fox The highlight package contains classes to provide keyword in context features typically used to highlight search terms in the text of results pages. The Highlight", Store.YES)).insert();
				isession.newDocument().add(MyField.keyword("tv", "slow fox white fox")).insert();
				isession.newDocument().add(MyField.keyword("tv", "fast wolf red wolf")).insert();

				return null;
			}
		});

		final Searcher searcher = cen.newSearcher();
		final SearchResponse response = searcher.createRequest("").find();
		final DocHighlighter hl = response.createHighlighter("tv", "fox") ;
		
		response.eachDoc(new EachDocHandler<Void>() {
			public Void handle(EachDocIterator iter) {
				try {
					while (iter.hasNext()) {
						Debug.line(hl.asString(iter.next()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidTokenOffsetsException e) {
					e.printStackTrace();
				}
				return null;
			}
		});

	}

}
