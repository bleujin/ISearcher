package net.ion.nsearcher.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.rest.formater.SearchResponseFormater;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.radon.core.ContextParam;


@Path("/")
public class SearchLet {


	@Path("/search.{format}")
	@GET
	public StreamingOutput search(@ContextParam("CENTRAL") Central central, @DefaultValue("html") @PathParam("format") String format, @FormParam("query") String query, @FormParam("sort") String sort, 
				@DefaultValue("0") @FormParam("skip") int skip, @DefaultValue("100") @FormParam("offset") int offset) throws Exception {
		
		SearchResponse response = central.newSearcher().createRequest(query).ascending(sort).skip(skip).offset(offset).find() ;
		
		Class clz = Class.forName("net.ion.nsearcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchResponseFormater af = (SearchResponseFormater) clz.newInstance();
		return af.outputStreaming(response);
	}


}
