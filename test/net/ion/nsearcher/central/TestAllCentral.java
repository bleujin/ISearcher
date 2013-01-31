package net.ion.nsearcher.central;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.framework.util.Debug;

public class TestAllCentral {
	
	public static Test suite(){
		System.setProperty(Debug.PROPERTY_KEY, "off") ;
		TestSuite ts = new TestSuite("ALL Central") ;
		
		ts.addTestSuite(TestCentral.class) ;
		ts.addTestSuite(TestIndexWriteConfig.class) ;
		
		return ts ;
	}

}
