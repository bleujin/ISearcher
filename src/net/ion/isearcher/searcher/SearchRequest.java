package net.ion.isearcher.searcher;

import static net.ion.isearcher.common.IKeywordField.ISALL_FIELD;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.ion.framework.db.Page;
import net.ion.framework.util.CaseInsensitiveHashMap;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.util.SortExpression;

import org.apache.ecs.xml.XML;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;

public class SearchRequest implements ISearchRequest {

	private final String query;
	private Query queryObj;
	private final String sort;
	private final Analyzer analyzer;
	private final QueryParser parser;

	private Filter filter;
	private Map<String, Object> param = new CaseInsensitiveHashMap<Object>();
	private Page page = Page.TEN;
	public final static String BLANK_QUERY_STRING = "<null/>";

	protected SearchRequest(Query queryObj, String sortfield, Analyzer analyzer) {
		this.query = queryObj.toString();
		this.queryObj = queryObj;
		this.sort = sortfield;
		this.analyzer = analyzer;
		this.parser = new QueryParser(Version.LUCENE_36, ISALL_FIELD, analyzer);
	}

	protected SearchRequest(String query, String sortfield, Analyzer analyzer, QueryParser parser) throws ParseException {
		this.query = query;
		this.queryObj = isBlankExpression(query) ? new MatchAllDocsQuery() : parser.parse(query);
		this.sort = sortfield;
		this.analyzer = analyzer;
		this.parser = parser;
	}

	private boolean isBlankExpression(String query) {
		return BLANK_QUERY_STRING.equalsIgnoreCase(StringUtil.deleteWhitespace(query)) || StringUtil.isBlank(query);
	}

	protected SearchRequest(String query, String sortfield, Analyzer analyzer) throws ParseException {
		this(query, sortfield, new StandardAnalyzer(Version.LUCENE_36), new QueryParser(Version.LUCENE_36, ISALL_FIELD, analyzer));
	}

	public static ISearchRequest test(String query) throws ParseException {
		return new SearchRequest(query, null, new CJKAnalyzer(Version.LUCENE_36));
	}

	public static ISearchRequest test(String query, String sortfield) throws ParseException {
		return new SearchRequest(query, sortfield, new CJKAnalyzer(Version.LUCENE_36));
	}

	public static ISearchRequest create(String query, String sortfield, Analyzer analyzer) throws ParseException {
//		try {
//			Analyzer modAnalyzer = analyzer;
//			if (analyzer instanceof MyKoreanAnalyzer) {
//				String[] stopword = ((MyKoreanAnalyzer) analyzer).getStopword();
//				modAnalyzer = new KoreanAnalyzer(Version.LUCENE_36, stopword);
//			}

			QueryParser parser = new QueryParser(Version.LUCENE_36, ISALL_FIELD, analyzer);
			parser.setLowercaseExpandedTerms(true);
			return new SearchRequest(query, sortfield, analyzer, parser);
//		} catch (IOException ex) {
//			throw new ParseException(ex.getMessage());
//		}
	}

	public static ISearchRequest create(Query query, String sortfield, Analyzer analyzer) throws ParseException {
		return new SearchRequest(query, sortfield, analyzer);
	}

	public Query getQuery() throws ParseException {
		return queryObj;
	}

	public Query parse(String query) throws ParseException {
		return parser.parse(query);
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public void setQueryFilter(String queryExpression) throws ParseException {
		this.filter = new QueryWrapperFilter(parser.parse(queryExpression));
	}

	public Filter getFilter() {
		return this.filter;
	}

	public void setParam(String key, Object value) {
		param.put(key, value);
	}

	public Object getParam(String key) {
		return param.get(key);
	}

	public ISearchRequest setPage(Page page) {
		this.page = page;
		return this;
	}

	public Page getPage() {
		return this.page;
	}

	public Sort getSort() {
		if (StringUtil.isBlank(sort)) {
			SortField sf = SortField.FIELD_SCORE;
			return new Sort(sf);
		}
		return new Sort(SortExpression.parse(sort));
	}

	public String getQueryExpression() {
		return query;
	}

	public String getSortExpression() {
		return this.sort;
	}

	public XML toXML() {
		XML request = new XML("request");
		request.addElement(new XML("query").addElement(query));
		request.addElement(new XML("sort").addElement(sort));

		XML page = new XML("page");
		page.addAttribute("listNum", String.valueOf(getPage().getListNum()));
		page.addAttribute("pageNo", String.valueOf(getPage().getPageNo()));
		page.addAttribute("screenCount", String.valueOf(getPage().getScreenCount()));
		request.addElement(page);

		XML analyzerXML = new XML("analyzer");
		analyzerXML.addElement(analyzer.toString());
		request.addElement(analyzerXML);

		XML filterXML = new XML("filter");
		filterXML.addElement(getFilter() == null ? "" : getFilter().toString());
		request.addElement(filterXML);

		XML params = new XML("params");
		Set<Entry<String, Object>> entrys = param.entrySet();
		for (Entry<String, Object> entry : entrys) {
			String value = entry.getValue() == null ? "" : entry.getValue().toString();
			params.addElement(new XML(entry.getKey()).addElement(value));
		}
		request.addElement(params);

		return request;
	}

	public String toString() {
		return "QUERY:" + query + ",Filter:" + filter + ",SORT:" + sort + ", Page:" + getPage();
	}
}
