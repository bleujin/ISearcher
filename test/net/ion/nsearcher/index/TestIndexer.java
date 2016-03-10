package net.ion.nsearcher.index;

import java.io.IOException;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class TestIndexer extends TestCase {

	private Central central;

	protected void setUp() throws Exception {
		super.setUp();
		this.central = CentralConfig.newRam().build();
	}

	protected void tearDown() throws Exception {
		central.close();
		super.tearDown();
	}

	public void testCreate() throws Exception {

		Indexer indexer = central.newIndexer();
		assertEquals(StandardAnalyzer.class, indexer.analyzer().getClass());
	}

	public void testAfterIndex() throws Exception {
		Searcher searcher = central.newSearcher();

		assertEquals(0, searcher.search("").size());

		central.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws IOException {
				isession.newDocument().insert();
				return null;
			}
		});

		SearchResponse sr = searcher.search("");
		assertEquals(1, sr.size());
	}

	public void testIndexJson() throws Exception {
		final JsonObject json = JsonObject.fromString("{name:'bleujin', age:20}");

		Indexer indexer = central.newIndexer();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws IOException {
				isession.newDocument().add(json).insert();
				return null;
			}
		});

		assertEquals(1, central.newSearcher().search("name:bleujin").size());
		assertEquals(1, central.newSearcher().search("age:20").size());

		assertEquals(1, central.newSearcher().search("bleujin").size());
		assertEquals(1, central.newSearcher().search("20").size());
	}

}
