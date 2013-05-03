package net.ion.nsearcher.config;

import net.ion.framework.util.StringUtil;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class SearchConfig {

	private final Version version;
	private final Analyzer queryAnalyzer ;
	private final String defaultSearchFieldName ;
	
	SearchConfig(Version version, Analyzer queryAnalyzer, String defaultSearchFieldName) {
		this.version = version ;
		this.queryAnalyzer = queryAnalyzer ;
		this.defaultSearchFieldName = defaultSearchFieldName ;
	}

	public Analyzer queryAnalyzer() {
		return queryAnalyzer;
	}

	public String defaultSearchFieldName() {
		return defaultSearchFieldName;
	}

	public Query parseQuery(Analyzer analyzer, String query) throws ParseException {
		QueryParserWithNumericRange parser = new QueryParserWithNumericRange(version, defaultSearchFieldName(), analyzer) ;
		return parser.parse(query) ;
	}
	
}


class QueryParserWithNumericRange extends QueryParser {

	protected QueryParserWithNumericRange(CharStream stream) {
		super(stream);
	}
	
	public QueryParserWithNumericRange(Version version, String defaultField, Analyzer defaultAnalyzer) {
		super(version, defaultField, defaultAnalyzer) ;
	}

	@Override
	public Query getRangeQuery(String field, String part1, String part2, boolean inclusive) throws ParseException{
		
		if (isNumeric(part1) && isNumeric(part2)) {
			return NumericRangeQuery.newLongRange(field, toLong(part1), toLong(part2), inclusive, inclusive) ;
		} else {
			return super.getRangeQuery(field, part1, part2, inclusive) ;
		}
	}
	
	private static boolean isNumeric(String str){
	  return str.matches("[-+]\\d+");  //match a number with optional '-' and decimal.
	}
	
	private static long toLong(String part){
		if (StringUtil.isBlank(part)) return 0L ;
		return part.startsWith("+") ? Long.parseLong(part.substring(1)) : Long.parseLong(part) ;
	}
	
	
}
