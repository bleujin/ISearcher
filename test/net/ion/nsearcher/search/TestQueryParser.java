package net.ion.nsearcher.search;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.util.Version;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import junit.framework.TestCase;

public class TestQueryParser extends ISTestCase{

	private Central cen = null ; 
	public void setUp() throws Exception {
		super.setUp() ;
	}

	@Override
	protected void tearDown() throws Exception {
		cen.close();
		super.tearDown();
	}
	
	
	public void testTermRequest() throws Exception {
		cen = sampleTestDocument() ;
		String qstring = "id:/m/1234";
		SearchRequest request = cen.newSearcher().createRequestByTerm("id", "/m/1234") ;

		assertEquals(qstring, request.query().toString());
	}
	
		
}
