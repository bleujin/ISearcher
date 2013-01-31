package net.ion.crawler.util;

import java.net.URL;

import junit.framework.TestCase;

public class TestRobotUtil extends TestCase{

	String testPath = "/ics/123.htm" ;

	public void testNewRobot() throws Exception {
		Robot r = new Robot(new URL("http://localhost:8090/robots.txt")) ;
		
		boolean result = r.isAllowedToVisit(testPath) ;
		assertEquals(false, result) ;
	}

	public void testNewRobot3() throws Exception {
		Robot r = new Robot(new URL("http://localhost:8090/dddd/xx.xxx")) ;
		
		boolean result = r.isAllowedToVisit(testPath) ;
		assertEquals(false, result) ;
	}

	public void testDisAllowALL() throws Exception {
		Robot r = new Robot(new StringBuffer("User-agent: *\ndisallow: /ics\n")) ;
		
		boolean result = r.isAllowedToVisit(testPath) ;
		assertEquals(false, result) ;
	}


	public void testAllowICS() throws Exception {
		Robot r = new Robot(new StringBuffer("User-agent: *\nallow: /ics\n")) ;
		
		boolean result = r.isAllowedToVisit(testPath) ;
		assertEquals(true, result) ;
	}

}
