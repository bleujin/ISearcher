package net.ion.nsearcher.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ion.framework.rest.IMapListRepresentationHandler;
import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.reader.InfoReader.InfoHandler;
import net.ion.radon.core.annotation.DefaultValue;
import net.ion.radon.core.annotation.PathParam;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class InfoLet extends SearchResource{

	@Get
	public Representation info(@DefaultValue("html") @PathParam("format") String format) throws Exception {
		InfoReader reader = getInfoReader() ;
		
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
		
		Class clz = Class.forName("net.ion.framework.rest." + format.toUpperCase() + "Formater");
		IMapListRepresentationHandler af = (IMapListRepresentationHandler) clz.newInstance();
		
		List<Map<String, ? extends Object>> result = new ArrayList<Map<String, ? extends Object>>() ;
		result.add(infoMap) ;
		return af.toRepresentation(IRequest.EMPTY_REQUEST, result, IResponse.EMPTY_RESPONSE) ;
		
	}
	
	
}
