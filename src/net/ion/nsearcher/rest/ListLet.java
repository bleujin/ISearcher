package net.ion.nsearcher.rest;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.rest.formater.SearchDocumentFormater;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.radon.core.ContextParam;

@Path("")
public class ListLet {

	@Path("/list.{format}")
	@GET
	public StreamingOutput listInfo(@ContextParam("CENTRAL") Central central, @DefaultValue("0") @FormParam("skip") int skip, @DefaultValue("100") @FormParam("offset") int offset, 
				@DefaultValue("html") @PathParam("format") String format) throws Exception {

		SearchResponse response = central.newSearcher().createRequest("").skip(skip).offset(offset).find();
		
		List<ReadDocument> docs = response.getDocument() ;
		Class clz = Class.forName("net.ion.nsearcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchDocumentFormater af = (SearchDocumentFormater) clz.newInstance();
		
		return af.outputStreaming(docs) ;
	}

}
