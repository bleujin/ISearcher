package net.ion.nsearcher.search;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.nsearcher.impl.FieldTest;
import net.ion.nsearcher.impl.PostProcessorTest;
import net.ion.nsearcher.impl.SearcherTest;

public class TestAllSearcher extends TestSuite{

	public static Test suite() {
		TestSuite ts = new TestSuite("All Searcher");
		
		ts.addTestSuite(FirstTest.class) ;
		ts.addTestSuite(FieldTest.class);

		ts.addTestSuite(PostProcessorTest.class);
		ts.addTestSuite(FilterTest.class);

		ts.addTestSuite(SearcherTest.class);
		ts.addTestSuite(ReaderTest.class);
		ts.addTestSuite(SortTest.class);
		
		return ts;
	}
}
