package net.ion.isearcher.indexer;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.isearcher.indexer.channel.TestCollectorExecutor;
import net.ion.isearcher.indexer.channel.TestDocument;
import net.ion.isearcher.indexer.channel.TestStackFile;

public class TestAllIndexer {

	public static Test suite(){
		TestSuite ts = new TestSuite() ;
		
		
		ts.addTestSuite(TestCollectorExecutor.class) ;
		
		
		ts.addTestSuite(PolicyTest.class) ;
		ts.addTestSuite(RollbackTest.class) ;
		ts.addTestSuite(TestDocument.class) ;
		ts.addTestSuite(TestStackFile.class) ;
		ts.addTestSuite(IndexerTest.class) ;
		
		return ts ;
	} 
}
