package net.ion.isearcher.searcher;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.isearcher.impl.FieldTest;
import net.ion.isearcher.impl.PostProcessorTest;
import net.ion.isearcher.impl.SearcherTest;

public class TestAllSearcher extends TestSuite{

	public static Test suite() {
		TestSuite ts = new TestSuite("SearcherTest");
		
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
