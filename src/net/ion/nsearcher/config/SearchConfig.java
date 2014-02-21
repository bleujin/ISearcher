package net.ion.nsearcher.config;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class SearchConfig {

	private final Version version;
	private Analyzer queryAnalyzer;
	private final String defaultSearchFieldName;

	SearchConfig(Version version, Analyzer queryAnalyzer, String defaultSearchFieldName) {
		this.version = version;
		this.queryAnalyzer = queryAnalyzer;
		this.defaultSearchFieldName = defaultSearchFieldName;
	}

	public Analyzer queryAnalyzer() {
		return queryAnalyzer;
	}

	public SearchConfig queryAnalyzer(Analyzer queryAnalyzer) {
		this.queryAnalyzer = queryAnalyzer;
		return this;
	}

	public String defaultSearchFieldName() {
		return defaultSearchFieldName;
	}

	public Query parseQuery(String query) throws ParseException {
		return parseQuery(queryAnalyzer(), query);
	}

	public Query parseQuery(Analyzer analyzer, String query) throws ParseException {
		QueryParserWithNumericRange parser = new QueryParserWithNumericRange(version, defaultSearchFieldName(), analyzer);
		return parser.parse(query);
	}

}

class QueryParserWithNumericRange extends ExtendableQueryParser {

	public QueryParserWithNumericRange(Version version, String defaultField, Analyzer defaultAnalyzer) {
		super(version, defaultField, defaultAnalyzer);
	}

	@Override
	public Query getRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) throws ParseException {
		if (isNumeric(part1) && isNumeric(part2)) {
			return NumericRangeQuery.newLongRange(field, toLong(part1), toLong(part2), startInclusive, endInclusive);
		} else {
			return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
		}
	}
	
	@Override
	public Query newTermQuery(org.apache.lucene.index.Term t) {
		Debug.debug(t);
		return super.newTermQuery(t) ;
	}

	private static boolean isNumeric(String str) {
		return str.matches("[-+]\\d+"); // match a number with optional '-' and decimal.
	}

	private static long toLong(String part) {
		if (StringUtil.isBlank(part))
			return 0L;
		return part.startsWith("+") ? Long.parseLong(part.substring(1)) : Long.parseLong(part);
	}

}
