package net.ion.nsearcher.search.filter;

import junit.framework.TestCase;

public class TestFilterUtil extends TestCase {

	public void testCreateBuilder() throws Exception {
		FilterUtil.newBuilder().term("name", "bleujin").between("age", 20, 30).andBuild() ;
	}
	
}
