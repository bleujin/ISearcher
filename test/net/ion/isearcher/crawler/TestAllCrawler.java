package net.ion.isearcher.crawler;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.isearcher.crawler.auth.LoginURLInfoTest;
import net.ion.isearcher.crawler.filter.FilterTest;
import net.ion.isearcher.crawler.parser.link.ExtractLinkTest;
import net.ion.isearcher.crawler.parser.link.LinkExtractorBleujinTest;
import net.ion.isearcher.crawler.util.HashFunctionTest;
import net.ion.isearcher.crawler.util.SmartInputStreamTest;

public class TestAllCrawler extends TestSuite {

	public static Test suite() {
		TestSuite ts = new TestSuite("CrawlTest");

		ts.addTestSuite(LoginURLInfoTest.class) ;
		
		ts.addTest(FilterTest.suite()) ;
		
		ts.addTestSuite(ExtractLinkTest.class) ;
		ts.addTestSuite(LinkExtractorBleujinTest.class) ;
		
		// util
		ts.addTestSuite(HashFunctionTest.class);
		ts.addTestSuite(SmartInputStreamTest.class);
		
		return ts;
	}


}
