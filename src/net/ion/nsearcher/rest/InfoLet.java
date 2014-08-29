package net.ion.nsearcher.rest;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.reader.InfoReader.InfoHandler;
import net.ion.radon.core.ContextParam;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;

@Path("/")
public class InfoLet {

	@Path("/info.{format}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject info(@ContextParam("CENTRAL") Central central ,@DefaultValue("html") @PathParam("format") String format) throws Exception {
		InfoReader reader = central.newReader() ;
		
		Map<String, Object> infoMap = reader.info(new InfoHandler<Map<String, Object>>() {
			@Override
			public Map<String, Object> view(IndexReader ireader, DirectoryReader dreader) throws IOException {
				Map<String, Object> result = MapUtil.newMap() ;
				
				result.put("directory", dreader.directory()) ;
				result.put("current version", dreader.getVersion()) ;
				result.put("indexExists", DirectoryReader.indexExists(dreader.directory())) ;
				result.put("lastModified", DirectoryReader.listCommits(dreader.directory())) ;
				result.put("maxDoc", ireader.maxDoc()) ;
				result.put("numDoc", ireader.numDocs()) ;
				
				return result;
			}
		}) ;
		
		return JsonObject.fromObject(infoMap) ;
	}
	
	
}
