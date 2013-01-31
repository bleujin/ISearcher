package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ion.framework.parse.gson.Gson;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeWriter;
import net.ion.framework.util.XMLToJSON;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;

import org.apache.lucene.document.Fieldable;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;

public class SearchJSONFormater extends AbstractDocumentFormater implements SearchResponseFormater {

	public Representation toRepresentation(SearchResponse iresponse) throws IOException {

		try {
			SearchRequest irequest = iresponse.request();

			JsonObject result = new JsonObject();
			result.add("request", XMLToJSON.toJSONObject(irequest.toXML().toString()).asJsonObject("request"));
			result.add("response", XMLToJSON.toJSONObject(iresponse.toXML().toString()).asJsonObject("response"));
			toJSON(iresponse.getDocument(), result);

			JsonObject root = new JsonObject();
			root.add("result", result);

			StringWriter rw = new StringWriter();
			new Gson().toJson(root, rw);

			return new StringRepresentation(rw.getBuffer(), MediaType.APPLICATION_JSON, Language.ALL, CharacterSet.UTF_8);
		} catch (SQLException e) {
			throw new ResourceException(e);
		}
		/*
		 * ISearchRequest irequest = iresponse.getRequest() ; RopeWriter rw = toJSONString(irequest, iresponse) ;
		 * 
		 * Representation result = new RopeRepresentation(rw.getRope(), getMediaType()); result.setCharacterSet(CharacterSet.UTF_8) ; return result;
		 */
	}

	/*
	 * 
	 * private RopeWriter toJSONString(ISearchRequest irequest, ISearchResponse iresponse) throws CorruptIndexException, IOException { iresponse.getRequest() ;
	 * 
	 * RopeWriter rw = new RopeWriter() ; rw.write("{\"result\":{") ; rw.write("\"nodes\":") ; rw.write(toRope(iresponse.getDocument())) ; rw.write(",", convert(irequest.toXML())) ; rw.write(",", convert(iresponse.toXML())) ; rw.write("}}") ; return rw ; }
	 * 
	 * 
	 * private CharSequence convert(XML xml) throws IOException { return XMLToJSON.toJSONObject(xml.toString()).toString() ; }
	 */
	private JsonObject toJSON(List<MyDocument> docs, JsonObject parent) throws SQLException {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (MyDocument doc : docs) {
			Map<String, Object> node = new HashMap<String, Object>();
			List<Fieldable> fields = doc.getFields();
			for (Fieldable field : fields) {
				if(node.containsKey(field.name())){
					if(field.stringValue().length() > ((String)node.get(field.name())).length()){
						node.put(field.name(), field.stringValue());
					}
				} else {
					node.put(field.name(), field.stringValue());
				}
			}
			nodeList.add(node);
		}
		parent.add("nodes", JsonParser.fromList(nodeList));
		return parent;
	}

	public Rope toRope(List<MyDocument> docs) throws IOException {
		RopeWriter rw = new RopeWriter();
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (MyDocument doc : docs) {
			Map<String, Object> node = new HashMap<String, Object>();
			List<Fieldable> fields = doc.getFields();
			for (Fieldable field : fields) {
				node.put(field.name(), field.stringValue());
			}
			nodeList.add(node);
		}
		rw.write(JsonParser.fromList(nodeList).toString());
		return rw.getRope();
	}

	public MediaType getMediaType() {
		return MediaType.APPLICATION_JSON;
	}
}