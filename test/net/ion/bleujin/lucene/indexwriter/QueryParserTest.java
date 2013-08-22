package net.ion.bleujin.lucene.indexwriter;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;

import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.util.Version;

public class QueryParserTest extends ISTestCase {

	
	public void testStandard() throws Exception {
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "", new KoreanAnalyzer()) ;
		
		Debug.debug(parser.parse("name:bleujin")) ;
		Debug.debug(parser.parse("name>bleujin")) ;
		Debug.debug(parser.parse("name:[bleujin TO bleuhero]")) ;
		Debug.debug(parser.parse("NOT name:bleujin")) ;

		
		Debug.debug(parser.parse("bleujin bleuhero")) ;

	}
	
	
}
