package net.ion.nsearcher.central;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.index.collect.TestCollectThread;

public class TestAllCentral {
	
	public static Test suite(){
//		System.setProperty(Debug.PROPERTY_KEY, "off") ;
		TestSuite ts = new TestSuite("ALL Central") ;
		
		ts.addTestSuite(TestCentral.class) ;
		ts.addTestSuite(TestIndexConfig.class) ;
		ts.addTestSuite(TestSearchConfig.class);
		
		
		return ts ;
	}

}
