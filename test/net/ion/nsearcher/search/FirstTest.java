package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;

public class FirstTest extends TestCase{

	public void testBlankSearcher() throws Exception {
		Central c = CentralConfig.newRam().build() ;
		Searcher searcher = c.newSearcher() ;
		searcher.search("").debugPrint() ;
	}

}
