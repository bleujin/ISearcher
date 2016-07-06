package net.ion.nsearcher.index;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.nsearcher.common.TestReadDocument;
import net.ion.nsearcher.common.TestWriteDocument;
import net.ion.nsearcher.config.TestFieldIndexingStrategy;

public class TestAllIndexer {

	public static Test suite(){
		TestSuite ts = new TestSuite("All Indexer") ;
		
		ts.addTestSuite(TestDocument.class) ;
		ts.addTestSuite(TestWriteDocument.class) ;
		ts.addTestSuite(TestReadDocument.class) ;
		ts.addTestSuite(TestDocumentField.class);
		ts.addTestSuite(TestMerge.class) ;
		ts.addTestSuite(TestSpecialChar.class);
//		ts.addTestSutie(TestBoost.class) ;
		
		ts.addTestSuite(TestIndexer.class) ;
		ts.addTestSuite(TestIndexAnalyzer.class);
		ts.addTestSuite(TestIndexSession.class) ;
		
		ts.addTestSuite(TestFieldIndexingStrategy.class) ;
		ts.addTestSuite(TestIndexSchema.class) ;
		ts.addTestSuite(TestRollback.class) ;
		
		return ts ;
	} 
}
