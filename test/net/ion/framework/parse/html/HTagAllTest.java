package net.ion.framework.parse.html;

import junit.framework.Test;
import junit.framework.TestSuite;

public class HTagAllTest extends TestSuite{

	public static Test suite(){
		TestSuite ts = new TestSuite("Htag Test");

		ts.addTestSuite(HTMLTest.class) ;
		
		return ts ;
	}
}
