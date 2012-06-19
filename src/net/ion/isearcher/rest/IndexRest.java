package net.ion.isearcher.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.Page;
import net.ion.framework.rest.RopeRepresentation;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.rest.formater.SearchDocumentFormater;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public class IndexRest extends SearchResource {

	public IndexRest() {
		super();

		getVariants().add(new Variant(MediaType.TEXT_ALL));
	}

	public Representation get(Variant variant) {
		// super.get() ;

		try {
			Representation entity = makeRepresentation();
			return entity;

		} catch (Exception ex) {
			throw new ResourceException(ex);
		}
	}

	private Representation makeRepresentation() throws Exception {
		Request request = getRequest();
		Context context = getContext();

		String format = request.getAttributes().get("format").toString();
		Map<String, String> params = getParameterMap(request);
		List<MyDocument> docs = getMydocumentList(context, request);

		Class clz = Class.forName("net.ion.isearcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchDocumentFormater af = (SearchDocumentFormater) clz.newInstance();
		
		return  new RopeRepresentation(af.toRope(docs), af.getMediaType()); 
	}

	private List<MyDocument> getMydocumentList(Context context, Request request) throws CorruptIndexException, IOException {
		Map<String, String> params = getParameterMap(request);
		Page page = getPage(params) ;
		IndexReader reader = getIReader(context, request).getIndexReader();

		List<MyDocument> docs = new ArrayList<MyDocument>();
		for (int i = page.getStartLoc(); i < page.getEndLoc(); i++) {
			MyDocument mydoc = MyDocument.loadDocument(reader.document(i));
			docs.add(mydoc);
		}
		return docs;
	}

}
