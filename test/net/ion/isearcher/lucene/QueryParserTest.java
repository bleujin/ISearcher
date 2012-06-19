package net.ion.isearcher.lucene;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
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
	
	
	public void testIndexWriterOption() throws Exception {
		IWriter writer = makeIndexWriter(false) ;
		
//		Debug.debug(
//				writer.getMaxBufferedDeleteTerms(),  // -1 
//				writer.getRAMBufferSizeMB(),       // 16.0
//				writer.getMaxMergeDocs(),           // 2147483647
//				writer.getMaxSyncPauseSeconds(),  // 10.0
//				writer.getMergeFactor(),          // 10
//				writer.getTermIndexInterval(),    //128
//				writer.getWriteLockTimeout(),     // 1000
//				writer.getMaxFieldLength(),        // 10000
//				writer.getMaxBufferedDocs(),       // -1
//				writer.getDefaultWriteLockTimeout()  // 1000
//			) ;
		
	}
}
