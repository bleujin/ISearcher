package net.ion.isearcher.crawler.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FilterTest extends TestSuite{

	
	public static Test suite(){
		TestSuite ts = new TestSuite("FilterTest") ;
		
		ts.addTestSuite(BeginningPathFilterTest.class) ;
		ts.addTestSuite(FileExtensionFilterTest.class) ;
		ts.addTestSuite(ServerFilterTest.class) ;
		ts.addTestSuite(SubDomainFilterTest.class) ;
		
		
		return ts;
	}
	
	
}
