package net.ion.isearcher.searcher;

import static net.ion.isearcher.common.IKeywordField.ISALL_FIELD;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.ion.framework.db.Page;
import net.ion.framework.util.CaseInsensitiveHashMap;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.util.SortExpression;

import org.apache.ecs.xml.XML;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;

public class SearchTermRequest implements ISearchRequest{

	private String sort;
	private Analyzer analyzer ;
	
	private Filter filter ;
	private Map<String, Object> param = new CaseInsensitiveHashMap<Object>();
	private Page page = Page.TEN ;
	private QueryParser parser ;
	
	
	private Term term ;
	public SearchTermRequest(String fieldName, String value, String sort, Analyzer analyzer){
		this.term = new Term(fieldName, value) ;
		this.analyzer = analyzer ;
		this.parser = new QueryParser(Version.LUCENE_CURRENT, ISALL_FIELD, analyzer) ;
	}
	
	
	public final static ISearchRequest create(String field, String value, String sort, Analyzer analyzer) {
		return new SearchTermRequest(field, value, sort, analyzer) ;
	} 
	
	
	public Query getQuery() throws ParseException {
		return new TermQuery(term) ;
	}

	public Query parse(String query) throws ParseException{
		String fieldName = StringUtil.substringBefore(query, ":") ;
		String value = StringUtil.substringAfter(query, ":") ;
		return new TermQuery(new Term(fieldName, value)) ;
	}
	
	public void setFilter(Filter filter){
		this.filter = filter ;
	}
	
	public void setQueryFilter(String queryExpression) throws ParseException{
		this.filter = new QueryWrapperFilter(parser.parse(queryExpression)) ;
	}
	
	public Filter getFilter(){
		return this.filter ;
	}
	
	public void setParam(String key, Object value){
		param.put(key, value) ;
	}
	
	public Object getParam(String key){
		return param.get(key) ;
	}
	
	public ISearchRequest setPage(Page page){
		this.page = page ;
		return this ;
	}
	
	public Page getPage(){
		return this.page ;
	}
	
	public Sort getSort() {
		if (sort == null) {
			SortField sf = SortField.FIELD_SCORE;
			return new Sort(sf);
		}
		return new Sort(SortExpression.parse(sort));
	}

	public String getQueryExpression(){
		return term.toString() ;
	}
	
	public String getSortExpression(){
		return this.sort ;
	}
	
	public XML toXML(){
		XML request = new XML("request") ;
		request.addElement(new XML("query").addElement(term.toString())) ;
		request.addElement(new XML("sort").addElement(sort)) ;

		XML page = new XML("page") ;
		page.addAttribute("listNum", String.valueOf(getPage().getListNum()));
		page.addAttribute("pageNo", String.valueOf(getPage().getPageNo()));
		page.addAttribute("screenCount", String.valueOf(getPage().getScreenCount()));
		request.addElement(page) ;
		
		XML analyzerXML = new XML("analyzer") ;
		analyzerXML.addElement(analyzer.toString()) ;
		request.addElement(analyzerXML) ;
		
		XML filterXML = new XML("filter") ;
		filterXML.addElement(getFilter() == null ? "" : getFilter().toString()) ;
		request.addElement(filterXML) ;
		
		XML params = new XML("params") ;
		Set<Entry<String, Object>> entrys = param.entrySet() ;
		for (Entry<String, Object> entry : entrys) {
			String value = entry.getValue() == null ? "" : entry.getValue().toString() ;
			params.addElement(new XML(entry.getKey()).addElement(value)) ;
		}
		request.addElement(params) ;
		
		return request ;
	}
	
	public String toString(){
		return "QUERY[Term]:" + term.toString() + ",Filter:" + filter + ",SORT:" + sort + ", Page:" + getPage();
	}

}
