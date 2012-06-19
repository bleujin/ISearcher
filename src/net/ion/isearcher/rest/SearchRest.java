package net.ion.isearcher.rest;

import java.io.IOException;
import java.util.Map;

import net.ion.framework.db.Page;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.rest.formater.SearchResponseFormater;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.ISearchResponse;
import net.ion.isearcher.searcher.SearchRequest;

import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public class SearchRest extends SearchResource{
	
	public SearchRest(){
		super() ;
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
		Class clz = Class.forName("net.ion.isearcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchResponseFormater af = (SearchResponseFormater) clz.newInstance();
		return af.toRepresentation(getSearchResponse(context, request));
	}

	private ISearchResponse getSearchResponse(Context context, Request request) throws CorruptIndexException, IOException, ParseException {
		Map<String, String> params = getParameterMap(request);
		ISearcher searcher = getISearcher(context, request);
		String query = params.get("query") ;
		String sort = params.get("sort") ;
		Page page = getPage(params) ;
		
		ISearchRequest searchRequest = SearchRequest.create(query, sort, new KoreanAnalyzer());
		searchRequest.setPage(page) ;
		
		return searcher.search(searchRequest) ;
	}


}
