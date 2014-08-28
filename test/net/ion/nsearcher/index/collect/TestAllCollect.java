package net.ion.nsearcher.index.collect;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.nsearcher.index.TestPolicy;
import net.ion.nsearcher.index.channel.TestStackFile;



@Deprecated
public class TestAllCollect extends TestCase {
	public static Test suite(){
		TestSuite ts = new TestSuite("All Collector") ;
		
		ts.addTestSuite(TestPolicy.class) ;
		ts.addTestSuite(TestStackFile.class) ;
		
		ts.addTestSuite(TestCollectThread.class);
		
		return ts ;
	} 
}
