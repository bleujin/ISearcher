package net.ion.nsearcher.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.rest.IMapListRepresentationHandler;
import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.radon.core.annotation.DefaultValue;
import net.ion.radon.core.annotation.PathParam;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public class IndexLet extends SearchResource{

	@Post
	public Representation listInfo(@DefaultValue("html") @PathParam("format") String format) throws Exception {

		Indexer indexer = getIndexer() ;
		final Map<String, Object> map = getInnerRequest().getGeneralParameter();
		indexer.index(new IndexJob<Void>() {

			public Void handle(IndexSession session) throws Exception {
				WriteDocument doc = WriteDocument.testDocument();
				for (Entry<String, Object> entry : map.entrySet()) {
					doc.add(MyField.unknown(entry.getKey(), entry.getValue())) ;
				}
				session.insertDocument(doc) ;
				return null;
			}
		}) ;
		
		Class clz = Class.forName("net.ion.framework.rest." + format.toUpperCase() + "Formater");
		IMapListRepresentationHandler af = (IMapListRepresentationHandler) clz.newInstance();
		
		List<Map<String, ? extends Object>> result = new ArrayList<Map<String, ? extends Object>>() ;
		result.add(map) ;
		return af.toRepresentation(IRequest.EMPTY_REQUEST, result, IResponse.EMPTY_RESPONSE) ;

	}
}
