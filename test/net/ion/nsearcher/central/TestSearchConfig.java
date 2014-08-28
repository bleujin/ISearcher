package net.ion.nsearcher.central;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import net.ion.framework.util.WithinThreadExecutor;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import junit.framework.TestCase;

public class TestSearchConfig extends TestCase{

	public void testDefault() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		assertEquals(StandardAnalyzer.class, cen.searchConfig().queryAnalyzer().getClass()) ;
		assertEquals(WithinThreadExecutor.class, cen.searchConfig().searchExecutor().getClass()) ;
		
	}
	
	
}
