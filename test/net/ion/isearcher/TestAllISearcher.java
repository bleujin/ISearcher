package net.ion.isearcher;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.framework.parse.html.HTagAllTest;
import net.ion.framework.util.Debug;
import net.ion.isearcher.crawler.TestAllCrawler;
import net.ion.isearcher.impl.TestAllImpl;
import net.ion.isearcher.indexer.TestAllIndexer;
import net.ion.isearcher.searcher.TestAllSearcher;

public class TestAllISearcher {

	public static Test suite(){
		System.setProperty(Debug.PROPERTY_KEY, "off") ;
		TestSuite ts = new TestSuite("ISearcher ALL") ;
		
		
		
		ts.addTestSuite(TestCentral.class) ;
//		
		ts.addTest(TestAllCrawler.suite()) ;
		ts.addTest(TestAllIndexer.suite()) ;
		ts.addTest(TestAllSearcher.suite()) ;
		ts.addTest(TestAllImpl.suite()) ;
		
		return ts ;
	}
}
