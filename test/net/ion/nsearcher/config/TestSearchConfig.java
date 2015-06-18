package net.ion.nsearcher.config;

import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.Searcher;
import junit.framework.TestCase;

public class TestSearchConfig extends TestCase {

	private Central central;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.central = CentralConfig.newRam().build();
	}

	@Override
	protected void tearDown() throws Exception {
		central.close();
		super.tearDown();
	}

	public void testOptionAttr() throws Exception {

		Searcher first = central.newSearcher();
		first.config().attr("offset", 10);
		first.config().attr("sort", "id desc");

		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().insert();
				return null;
			}
		});

		Searcher second = central.newSearcher();

		assertEquals(10, first.config().attrAsInt("offset", 0));
		assertEquals(10, second.config().attrAsInt("offset", 0));

		assertEquals("id desc", first.config().attrAsString("sort", ""));
		assertEquals("id desc", second.config().attrAsString("sort", ""));
	}
}
