package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ion.framework.db.Page;
import net.ion.framework.util.CaseInsensitiveHashMap;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.ReadDocument;

import org.apache.ecs.xml.XML;
import org.apache.lucene.document.DocumentStoredFieldVisitor;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.queryparser.classic.ParseException;
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
	private Set<String> columns = SetUtil.newSet() ;
	private Set<String> lazyColumns = SetUtil.newSet() ;
	
	SearchRequest(Searcher searcher, Query query){
		this.searcher = searcher ;
		this.query = query ;
	}
	
	public SearchRequest skip(int skip){
		this.skip = skip ;
		return this ;
	}
	
	public Searcher searcher(){
		return searcher ;
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
	
	public SearchRequest sort(String expr){
		if (StringUtil.isBlank(expr)) return this ;
		for (String e : StringUtil.split(expr, ",")) {
			sortExpression.add(e) ;
		}
		return this ;
	}

	public SearchRequest ascending(String field) {
		sortExpression.add(field);
		return this ;
	}

	public SearchRequest descending(String field) {
		sortExpression.add(field + " desc");
		return this ;
	}

	public SearchRequest ascendingNum(String field) {
		sortExpression.add(field + " _number");
		return this ;
	}

	public SearchRequest descendingNum(String field) {
		sortExpression.add(field + " _number desc");
		return this ;
	}



	public SearchRequest setParam(String key, Object value) {
		param.put(key, value);
		return this ;
	}

	public Set<String> paramKeys(){
		return param.keySet() ;
	}
	
	public Object getParam(String key) {
		return param.get(key);
	}
	
	public SearchRequest setFilter(Filter filter) {
		this.filter = filter;
		return this ;
		
	}

	public Filter getFilter() {
		return this.filter;
	}

	public SearchResponse find() throws IOException, ParseException{
		return searcher.search(this) ;
	}
	
	public ReadDocument findOne() throws IOException, ParseException {
		final List<ReadDocument> docs = find().getDocument();
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
		return toXML().toString() ;
	}

	public SearchRequest resetClone(int maxValue) {
		final SearchRequest copy = new SearchRequest(searcher, query);
		copy.filter = this.filter ;
		
		return copy.skip(0).offset(maxValue) ;
	}

	
	public SearchRequest selections(String... cols) {
		for (String col : cols) {
			this.columns.add(col) ;
		}
		return this;
	}

//	public SearchRequest lazySelections(String... cols) {
//		for (String col : cols) {
//			this.lazyColumns.add(col) ;
//		}
//		return this;
//	}

	
	public Set<String> selectorField(){
		return columns ;
	}
	
	public StoredFieldVisitor selector(){
		return columns.size() == 0 ? null : new DocumentStoredFieldVisitor(columns) ;
	}


}
