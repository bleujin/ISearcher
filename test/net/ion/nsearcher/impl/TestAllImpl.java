package net.ion.nsearcher.impl;

import net.ion.nsearcher.search.TestSearchFilter;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllImpl {
	public static Test suite(){
		TestSuite ts = new TestSuite("All Searcher Impl") ;
		
		ts.addTestSuite(TestDeamonIndexer.class) ;
		
		return ts ;
	} 
}
