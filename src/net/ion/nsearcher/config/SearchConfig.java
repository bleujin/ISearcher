package net.ion.nsearcher.config;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.IndexFieldType;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class SearchConfig {

	private final Version version;
	private Analyzer queryAnalyzer;
	private final String defaultSearchFieldName;
	private ExecutorService es;
	private final Map<String, Analyzer> analMap = MapUtil.newMap();
	private PerFieldAnalyzerWrapper wrapperAnalyzer;
	private Map<String, Object> attrs = MapUtil.newMap();

	SearchConfig(ExecutorService es, Version version, Analyzer queryAnalyzer, String defaultSearchFieldName) {
		this.es = es;
		this.version = version;
		this.queryAnalyzer = queryAnalyzer;
		this.defaultSearchFieldName = defaultSearchFieldName;
		this.wrapperAnalyzer = new PerFieldAnalyzerWrapper(queryAnalyzer);
	}

	public final static SearchConfig create(ExecutorService es, Version version, Analyzer queryAnalyzer, String defaultSearchFieldName) {
		return new SearchConfig(es, version, queryAnalyzer, defaultSearchFieldName);
	}

	public Analyzer queryAnalyzer() {
		return analMap.size() == 0 ? this.queryAnalyzer : this.wrapperAnalyzer;
	}

	public ExecutorService searchExecutor() {
		return this.es;
	}

	public SearchConfig queryAnalyzer(Analyzer queryAnalyzer) {
		this.queryAnalyzer = queryAnalyzer;
		if (this.analMap.size() > 0)
			this.wrapperAnalyzer = new PerFieldAnalyzerWrapper(this.queryAnalyzer, this.analMap);
		return this;
	}

	public SearchConfig fieldAnalyzer(String fieldName, Analyzer analyzer) {
		analMap.put(fieldName, analyzer);
		this.wrapperAnalyzer = new PerFieldAnalyzerWrapper(this.queryAnalyzer, this.analMap);
		return this;
	}

	public SearchConfig removeFieldAnalyzer(String fieldName) {
		if (!analMap.containsKey(fieldName))
			return this;

		analMap.remove(fieldName);
		this.wrapperAnalyzer = new PerFieldAnalyzerWrapper(this.queryAnalyzer, this.analMap);
		return this;
	}

	public String defaultSearchFieldName() {
		return defaultSearchFieldName;
	}

	public Query parseQuery(IndexConfig iconfig, String query) throws ParseException {
		return parseQuery(iconfig, queryAnalyzer(), query);
	}

	public Query parseQuery(IndexConfig iconfig, Analyzer analyzer, String query) throws ParseException {
		QueryParserWithNumericRange parser = new QueryParserWithNumericRange(iconfig.indexFieldType(), defaultSearchFieldName(), analyzer);
		return parser.parse(query);
	}

	public SearchConfig attr(String name, int value) {
		attrs.put(name, value);
		return this;
	}

	public SearchConfig attr(String name, String value) {
		attrs.put(name, value);
		return this;
	}

	public String attrAsString(String name, String dftValue) {
		return StringUtil.coalesce(StringUtil.toString(attrs.get(name)), dftValue);
	}

	public int attrAsInt(String name, int dftValue) {
		return NumberUtil.toInt(StringUtil.toString(attrs.get(name)), dftValue);
	}

}

class QueryParserWithNumericRange extends ExtendableQueryParser {

	private IndexFieldType indexFieldType;

	public QueryParserWithNumericRange(IndexFieldType indexFieldType, String defaultField, Analyzer defaultAnalyzer) {
		super(defaultField, defaultAnalyzer);
		this.indexFieldType = indexFieldType;
	}

	@Override
	public Query getRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) throws ParseException {
		if (indexFieldType.isNumericField(field))
			return NumericRangeQuery.newLongRange(field, toLong(part1), toLong(part2), startInclusive, endInclusive);

		if (part1 != null && part2 != null && isNumeric(part1) && isNumeric(part2)) {
			return NumericRangeQuery.newLongRange(field, toLong(part1), toLong(part2), startInclusive, endInclusive);
		} else {
			return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
		}
	}

	@Override
	public Query newTermQuery(org.apache.lucene.index.Term t) {
		return super.newTermQuery(t);
	}

	private static boolean isNumeric(String str) {
		return str.matches("[-+]\\d+"); // match a number with optional '-' and decimal.
	}

	private static long toLong(String part) {
		if (StringUtil.isBlank(part))
			return 0L;
		return part.startsWith("+") ? Long.parseLong(part.substring(1)) : Long.parseLong(part);
	}

	protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
		return super.getFieldQuery(field, queryText, quoted) ;
	}
}
