package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.ion.framework.db.Page;
import net.ion.framework.util.CaseInsensitiveHashMap;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.MyDocument;

import org.apache.ecs.xml.XML;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public class SearchRequest {

	private Query query ;
	private int skip  = 0 ;
	private int offset = 100;
	private List<String> sortExpression = ListUtil.newList();
	private Map<String, Object> param = new CaseInsensitiveHashMap<Object>();
	private Filter filter;
	
	private final Searcher searcher ;
	SearchRequest(Searcher searcher, Query query){
		this.searcher = searcher ;
		this.query = query ;
	}
	
	public SearchRequest skip(int skip){
		this.skip = skip ;
		return this ;
	}
	
	public Query query() {
		return query;
	}

	public SearchRequest page(Page page){
		this.skip(page.getStartLoc()).offset(page.getListNum()) ;
		return this ;
	}
	
	public SearchRequest offset(int offset){
		this.offset = offset ;
		return this ;
	}
	
	public int skip() {
		return skip;
	}

	public int offset() {
		return offset;
	}

	public Sort sort() {
		if (sortExpression.size() == 0) return Sort.RELEVANCE ;
		return new Sort(SortExpression.parse(StringUtil.join(sortExpression, ","))) ;	
	}

	public int limit() {
		return skip + offset;
	}

	public SearchRequest ascending(String field) {
		sortExpression.add(field);
		return this ;
	}

	public SearchRequest descending(String field) {
		sortExpression.add(field + " desc");
		return this ;
	}



	public void setParam(String key, Object value) {
		param.put(key, value);
	}

	public Object getParam(String key) {
		return param.get(key);
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return this.filter;
	}

	public SearchResponse find() throws IOException, ParseException{
		return searcher.search(this) ;
	}
	
	public MyDocument findOne() throws IOException, ParseException {
		final List<MyDocument> docs = find().getDocument();
		if (docs.size() == 0) return null ;
		return docs.get(0) ;
	}

	

	public XML toXML() {
		XML request = new XML("request");
		request.addElement(new XML("query").addElement(query.toString()));
		request.addElement(new XML("sort").addElement(sortExpression.toString()));

		XML page = new XML("page");
		page.addAttribute("skip", String.valueOf(skip()));
		page.addAttribute("offset", String.valueOf(offset()));
		request.addElement(page);

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
		return "QUERY:" + query + ",Filter:" + getFilter() + ",SORT:" + sort() + ", skip:" + skip() + ", offset:" + offset();
	}

	public SearchRequest resetClone(int maxValue) {
		return new SearchRequest(searcher, query).skip(0).offset(maxValue) ;
	}



}
