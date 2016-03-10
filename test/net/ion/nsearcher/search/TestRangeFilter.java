package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.filter.FilterUtil;

public class TestRangeFilter extends TestCase {
	
	public void testBetween() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().keyword("start", "20160118-000000").keyword("end", "99991231-000000").insert();
				return null;
			}
		}) ;
		
//		central.newSearcher().createRequest("").find().debugPrint();
		central.newSearcher().createRequest("").setFilter(FilterUtil.newBuilder()
					.gt("start", "20160117-000000")
					// .filter(RangeFilterUtil.termRangeFilter("start", "20160117-000000", "99991231", true, true))
					.andBuild()).find().debugPrint(); 
		
	}

}
