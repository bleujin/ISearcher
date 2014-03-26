package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.filter.FilterUtil;

public class TestSearchRequest extends TestCase {

	
	private Central central;
	private Searcher searcher;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.central = CentralConfig.newRam().build() ;
		this.searcher = central.newSearcher() ;

		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("bleujin").keyword("name", "bleujin").number("int", 1).unknown("age", 30).text("explain", "my name is bleujin").update() ;
				isession.newDocument("hero").keyword("name", "hero").number("int", 2).unknown("age", 25).text("explain", "my address is seoul").insert() ;
				isession.newDocument("jin").keyword("name", "jin").number("int", 3).unknown("age", "35").text("explain", "this is not evil and not good").insert(); ;
				isession.newDocument("jini").keyword("name", "jini").number("int", 4).unknown("age", 40).text("explain", "orange for oracle").update(); ;
				return null;
			}
		}) ;
	}
	
	public void testGetField() throws Exception {
		ReadDocument doc = searcher.createRequest("bleujin").findOne();
		String[] fields = doc.getFieldNames() ;
		assertEquals(2, fields.length); // except text field(default strategy not store texttype)
	}
	
	public void testTerm() throws Exception {
		searcher.createRequest("").setFilter(FilterUtil.newBuilder().term("name", "jin").gte("int", 3).andBuild()).find().debugPrint();
	}
	
	public void testSearchFilter() throws Exception {
		assertEquals(2, searcher.andFilter(FilterUtil.newBuilder().gte("int", 3).andBuild()).createRequest("").find().size()) ;
		assertEquals(2, searcher.createRequest("").find().size());
		
		assertEquals(4, central.newSearcher().createRequest("").find().size()) ;
	}
	
}
