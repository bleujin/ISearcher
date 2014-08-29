package net.ion.nsearcher.rest;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.radon.core.ContextParam;

import org.jboss.resteasy.spi.HttpRequest;

@Path("/")
public class IndexLet {

	@Path("/index.{format}")
	@POST
	public String indexParam(@ContextParam("CENTRAL") Central central,  @DefaultValue("html") @PathParam("format") String format, @Context HttpRequest request) throws Exception {
		Indexer indexer = central.newIndexer();
		final MultivaluedMap<String, String> map = request.getFormParameters();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument();
				for (Entry<String, List<String>> entry : map.entrySet()) {
					doc.add(MyField.unknown(entry.getKey(), entry.getValue())) ;
				}
				isession.insertDocument(doc) ;
				return null;
			}
		}) ;
		
		return "indexed" ;
	}
}
