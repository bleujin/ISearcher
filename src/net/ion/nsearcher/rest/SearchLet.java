package net.ion.nsearcher.rest;

import net.ion.nsearcher.rest.formater.SearchResponseFormater;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.radon.core.annotation.DefaultValue;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class SearchLet extends SearchResource{


	@Get
	public Representation search(@DefaultValue("html") @PathParam("format") String format, @FormParam("query") String query, @FormParam("sort") String sort, 
				@DefaultValue("0") @FormParam("skip") int skip, @DefaultValue("100") @FormParam("offset") int offset) throws Exception {
		
		SearchResponse response = getSearcher().createRequest(query).ascending(sort).skip(skip).offset(offset).find() ;
		
		Class clz = Class.forName("net.ion.nsearcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchResponseFormater af = (SearchResponseFormater) clz.newInstance();
		return af.toRepresentation(response);
	}


}
