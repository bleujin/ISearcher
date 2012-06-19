package net.ion.isearcher.searcher;

import net.ion.framework.db.Page;

import org.apache.ecs.xml.XML;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public interface ISearchRequest {

	public Query getQuery() throws ParseException  ;

	
	public Query parse(String query) throws ParseException ;

	public void setFilter(Filter filter) ;
	
	public void setQueryFilter(String query) throws ParseException ;
	
	public Filter getFilter() ;
	
	public void setParam(String key, Object value) ;
	
	public Object getParam(String key) ;
	
	public ISearchRequest setPage(Page page) ;
	
	public Page getPage() ;
	
	public Sort getSort() ;

	public String getQueryExpression() ;
	
	public String getSortExpression() ;
	
	public XML toXML() ;
}
