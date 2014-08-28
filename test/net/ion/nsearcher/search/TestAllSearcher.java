package net.ion.nsearcher.search;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.nsearcher.impl.TestField;
import net.ion.nsearcher.impl.TestPostProcessor;
import net.ion.nsearcher.index.TestReaderInfo;

public class TestAllSearcher extends TestSuite{

	public static Test suite() {
		TestSuite ts = new TestSuite("All Searcher");
		
		ts.addTestSuite(FirstTest.class) ;
		ts.addTestSuite(TestField.class);

		ts.addTestSuite(TestPostProcessor.class);
		ts.addTestSuite(TestFilter.class);

		ts.addTestSuite(TestSearcher.class);
		ts.addTestSuite(TestSearcherPaging.class) ;
		ts.addTestSuite(TestSearchFilter.class) ;
		ts.addTestSuite(TestQueryParser.class);
		ts.addTestSuite(TestReader.class);
		ts.addTestSuite(TestMultiSearcher.class);
		ts.addTestSuite(TestSortExpression.class);
		
		ts.addTestSuite(TestSearchRequest.class);
		ts.addTestSuite(TestSearchResponse.class);
		ts.addTestSuite(TestReaderInfo.class) ;

		return ts;
	}
}
