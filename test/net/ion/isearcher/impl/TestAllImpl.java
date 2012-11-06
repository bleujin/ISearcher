package net.ion.isearcher.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllImpl {
	public static Test suite(){
		TestSuite ts = new TestSuite() ;
		
		ts.addTestSuite(TestSearchFilter.class) ;
		ts.addTestSuite(TestDeamonIndexer.class) ;
		
		return ts ;
	} 
}
