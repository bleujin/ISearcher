package net.ion.isearcher.crawler.util;

import junit.framework.TestCase;

public class HashFunctionTest extends TestCase{
	
	
	public void testHashValue() throws Exception {
		HashFunction fun = new HashFunction() ;
		
		assertEquals(fun.hashWebContent("ABCDEFG1"), fun.hashWebContent("ABCDEFG")) ;
		assertEquals(false, fun.hashWebContent("B") == fun.hashWebContent("A")) ;
	}

}
