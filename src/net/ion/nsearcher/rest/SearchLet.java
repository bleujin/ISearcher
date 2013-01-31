package net.ion.nsearcher.rest;

import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.rest.formater.SearchResponseFormater;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.radon.core.annotation.DefaultValue;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class SearchLet extends SearchResource{


	@Get
	public Representation search(@DefaultValue("html") @PathParam("format") String format, @FormParam("query") String query, @FormParam("sort") String sort, 
				@DefaultValue("0") @FormParam("skip") int skip, @DefaultValue("100") @FormParam("offset") int offset) throws Exception {
		
		SearchRequest sreq = SearchRequest.create(query, sort, new StandardAnalyzer(SearchConstant.LuceneVersion));
		sreq.skip(skip).offset(offset) ;
		
		Class clz = Class.forName("net.ion.nsearcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchResponseFormater af = (SearchResponseFormater) clz.newInstance();
		return af.toRepresentation(getSearcher().search(sreq));
	}


}
