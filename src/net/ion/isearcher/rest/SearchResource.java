package net.ion.isearcher.rest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.ion.framework.db.Page;
import net.ion.framework.util.CaseInsensitiveHashMap;
import net.ion.framework.util.PathMaker;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.IReader;
import net.ion.isearcher.impl.ISearcher;

import org.apache.lucene.store.FSDirectory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.resource.ServerResource;

public class SearchResource extends ServerResource{

	public SearchResource(){
		super() ;
	}
	
	protected Map<String, String> getParameterMap(Request request){
		Form query = request.getResourceRef().getQueryAsForm() ;
		Map<String, String> params = new CaseInsensitiveHashMap<String>() ;
		for (Parameter param : query) {
			params.put(param.getName(), param.getValue()) ;
		}
		return params ;
	}
	
	protected Page getPage(Map<String, String> params) {
		int listNum = Integer.parseInt(StringUtil.defaultIfEmpty(params.get("listNum"), "10"));
		int pageNo = Integer.parseInt(StringUtil.defaultIfEmpty(params.get("pageNo"), "1"));
		Page page = Page.create(listNum, pageNo);
		return page;
	}
	
	protected IReader getIReader(Context context, Request request) throws IOException{
		return getCentral(context, request).newReader() ;
	}
	
	protected ISearcher getISearcher(Context context, Request request) throws IOException {
		return getCentral(context, request).newSearcher() ;
	}
	
	protected Central getCentral(Context context, Request request) throws IOException{
		Map<String, Object> attrs = context.getAttributes();
		String basePath = (String) attrs.get(ISearcherApplication.FileLocation);
		String dirNm = (String) (request.getAttributes().get("dir"));
		

		String dirPath = PathMaker.getFilePath(basePath, dirNm) ;
		Central central = Central.createOrGet(FSDirectory.open(new File(dirPath))) ;
		return central ;
	}
}
