package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.IndexFieldType;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import junit.framework.TestCase;

public class TestQueryParser extends TestCase{

	private Central cen = null ; 
	public void setUp() throws Exception {
		super.setUp() ;
		cen = CentralConfig.newRam().build() ;
	}

	@Override
	protected void tearDown() throws Exception {
		cen.close();
		super.tearDown();
	}
	
	
	public void testFieldRename() throws Exception {
		cen = CentralConfig.newRam().searchConfigBuilder().queryParser().build() ;
		
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().stext("name", "bleujin hi").insert() ;
				isession.newDocument().stext("name", "hero hi").insert() ;
				return null;
			}
		}) ;
		SearchConfig sconfig = cen.searchConfig();
		RenameQueryParser qparser = new RenameQueryParser(cen.indexConfig().indexFieldType(), "age", sconfig.queryAnalyzer()).renameField("myname", "name") ;
		cen.newSearcher().createRequest(qparser.parse("name:bleujin")).find().debugPrint("name");
		cen.newSearcher().createRequest(qparser.parse("myname:bleujin")).find().debugPrint("name");
		
	}
	
	
	
	public void testTermRequest() throws Exception {
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				return isession.newDocument().keyword("id", "/m/1234").insertVoid() ;
			}
		}) ;
		
		String qstring = "id:/m/1234";
		SearchRequest request = cen.newSearcher().createRequestByTerm("id", "/m/1234") ;
		assertEquals(qstring, request.query().toString());
	}
	
	public void testBlankTerm() throws Exception {
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().insert() ;
				isession.newDocument("has").keyword("id", "").insert() ;
				return null ;
			}
		}) ;
		
		cen.newSearcher().createRequest("*:* AND -id:[* TO *]").find().debugPrint();
		
		
	}
	
	public void testRange() throws Exception {
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					isession.newDocument(i * 3 + "").unknown("num", ""+ i * 3).insert() ;
				}
				return null ;
			}
		}) ;
		
		cen.newSearcher().createRequest("num:[+2 TO +12]").find().debugPrint(); 
	}
		
}

//getFieldQuery
//getFuzzyQuery
//getPrefixQuery
//getRangeQuery
//getRegexpQuery
//getWildcardQuery
class RenameQueryParser extends ExtendableQueryParser {

	private IndexFieldType indexFieldType;
	private Map<String, String> renameField = MapUtil.newCaseInsensitiveMap() ;

	public RenameQueryParser(IndexFieldType indexFieldType, String defaultField, Analyzer defaultAnalyzer) {
		super(defaultField, defaultAnalyzer);
		this.indexFieldType = indexFieldType;
	}

	
	public RenameQueryParser renameField(String newName, String sourceName){
		renameField.put(newName, sourceName) ;
		return this ;
	}
	
	private String fieldName(String fname){
		return ObjectUtil.coalesce(renameField.get(fname), fname) ;
	}
	
	// 
	protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException {
		return super.getFieldQuery(fieldName(field), queryText, slop) ;
	}
	
	protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
		return super.getFieldQuery(fieldName(field), queryText, quoted) ;
	}
	
	protected Query getFunnzyQuery(String field, String termStr, float minSimilarity) throws ParseException{
		return super.getFuzzyQuery(fieldName(field), termStr, minSimilarity) ;
	}
	
	protected Query getPrefixQuery(String field, String termStr) throws ParseException{
		return super.getPrefixQuery(fieldName(field), termStr) ;
	}
	
	protected Query getRegexpQuery(String field, String termStr) throws ParseException{
		return super.getRegexpQuery(fieldName(field), termStr) ;
	}
	
	protected Query getWildcardQuery(String field, String termStr) throws ParseException{
		return super.getWildcardQuery(fieldName(field), termStr) ;
	}
	
	
	
	@Override
	public Query getRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) throws ParseException {
		if (indexFieldType.isNumericField(fieldName(field)))
			return NumericRangeQuery.newLongRange(fieldName(field), toLong(part1), toLong(part2), startInclusive, endInclusive);

		if (part1 != null && part2 != null && isNumeric(part1) && isNumeric(part2)) {
			return NumericRangeQuery.newLongRange(fieldName(field), toLong(part1), toLong(part2), startInclusive, endInclusive);
		} else {
			return super.getRangeQuery(fieldName(field), part1, part2, startInclusive, endInclusive);
		}
	}
	
	//
	
	

	private static boolean isNumeric(String str) {
		return str.matches("[-+]\\d+"); // match a number with optional '-' and decimal.
	}

	private static long toLong(String part) {
		if (StringUtil.isBlank(part))
			return 0L;
		return part.startsWith("+") ? Long.parseLong(part.substring(1)) : Long.parseLong(part);
	}

}
