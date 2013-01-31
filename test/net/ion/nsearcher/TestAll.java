package net.ion.nsearcher;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.central.TestAllCentral;
import net.ion.nsearcher.impl.TestAllImpl;
import net.ion.nsearcher.index.TestAllIndexer;
import net.ion.nsearcher.search.TestAllSearcher;

public class TestAll {

	public static Test suite(){
		System.setProperty(Debug.PROPERTY_KEY, "off") ;
		TestSuite ts = new TestSuite("ISearcher ALL") ;
		
		ts.addTest(TestAllCentral.suite()) ;
		ts.addTest(TestAllIndexer.suite()) ;
		ts.addTest(TestAllSearcher.suite()) ;
		ts.addTest(TestAllImpl.suite()) ;
		
		return ts ;
	}
}
