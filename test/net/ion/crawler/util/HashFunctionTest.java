package net.ion.crawler.util;

import junit.framework.TestCase;
import net.ion.nsearcher.common.HashFunction;

public class HashFunctionTest extends TestCase{
	
	
	public void testHashValue() throws Exception {
		HashFunction fun = new HashFunction() ;
		
		assertEquals(fun.hashWebContent("ABCDEFG1"), fun.hashWebContent("ABCDEFG")) ;
		assertEquals(false, fun.hashWebContent("B") == fun.hashWebContent("A")) ;
	}

}
