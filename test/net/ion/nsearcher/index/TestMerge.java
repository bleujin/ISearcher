package net.ion.nsearcher.index;

import net.ion.framework.util.Debug;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

public class TestMerge extends TestCase {
	
	public void testFirst() throws Exception {
		IndexWriterConfig iwconfig = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer()) ;
		MergePolicy mpolicy = iwconfig.getMergePolicy();
		Debug.line(mpolicy) ;
		
		
	}

}
