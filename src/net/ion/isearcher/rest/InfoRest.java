package net.ion.isearcher.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.rest.IMapListRepresentationHandler;
import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.isearcher.impl.IReader;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public class InfoRest extends SearchResource{
	
	public InfoRest(){
		super() ;
		getVariants().add(new Variant(MediaType.TEXT_ALL));
	}

	public Representation get(Variant variant) {
		try {
			
			Representation entity = makeRepresentation();
			return entity;

		} catch (RepositoryException ex) {
			throw new ResourceException(ex) ;
		} catch (IOException ex) {
			throw new ResourceException(ex) ;
		} catch (Exception ex) {
			throw new ResourceException(ex) ;
		}
	}

	private Representation makeRepresentation() throws IOException, ClassNotFoundException, ResourceException, InstantiationException, IllegalAccessException {
		Request request = getRequest();
		Context context = getContext();
		
		IReader reader = getIReader(context, request) ;
		IndexReader ireader = reader.getIndexReader() ;

		Map<String, Object> infoMap = new HashMap<String, Object>() ;
		Directory dir = ireader.directory();
		infoMap.put("directory", dir) ;
		infoMap.put("current version", ireader.getCurrentVersion(dir)) ;
		infoMap.put("directory version", ireader.getVersion()) ;
		infoMap.put("indexExists", ireader.indexExists(dir)) ;
		infoMap.put("isOptimized", ireader.isOptimized()) ;
		infoMap.put("lastModified", new Date(ireader.lastModified(dir))) ;
		infoMap.put("maxDoc", ireader.maxDoc()) ;
		infoMap.put("numDoc", ireader.numDocs()) ;
		
		String format = request.getAttributes().get("format").toString();
		Class clz = Class.forName("net.ion.framework.rest." + format + "Formater");
		IMapListRepresentationHandler af = (IMapListRepresentationHandler) clz.newInstance();
		
		
		List<Map<String, ? extends Object>> nodes = new ArrayList<Map<String, ? extends Object>>() ;
		nodes.add(infoMap) ;

		return af.toRepresentation(IRequest.EMPTY_REQUEST, nodes, IResponse.EMPTY_RESPONSE) ;
		
//		return  new RopeRepresentation(af.toRope(docs), af.getMediaType());
//		
//		StringBuffer buffer = new StringBuffer() ;
//		for (Entry entry : infoMap.entrySet()) {
//			buffer.append(entry.getKey() + " : " + entry.getValue() + "<br/>") ;
//		}
//		
//		Representation entity = new StringRepresentation(buffer.toString(), MediaType.TEXT_HTML);
//		entity.setCharacterSet(CharacterSet.UTF_8) ;
//		return entity;
	}
	
	
}
