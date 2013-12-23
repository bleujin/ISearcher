package net.ion.nsearcher.common;

import junit.framework.TestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.filter.TermFilter;

public class TestKeyword extends TestCase {

	public void testKeyword() throws Exception {
		Central cen = CentralConfig.newRam().build();
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument("test").keyword("path", "/bleujin/hero");
				isession.insertDocument(doc) ;
				return null;
			}
		});
		
		assertEquals(1, cen.newSearcher().createRequest("").setFilter(new TermFilter("path", "/bleujin/hero")).find().size()) ;
		cen.destroySelf() ;
	}

}
