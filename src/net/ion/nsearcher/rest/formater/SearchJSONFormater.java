package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import net.ion.framework.parse.gson.Gson;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.XMLToJSON;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.util.MyWriter;

import org.apache.lucene.index.IndexableField;

public class SearchJSONFormater implements SearchResponseFormater {

	@Produces(MediaType.APPLICATION_JSON)
	public StreamingOutput outputStreaming(final SearchResponse iresponse) throws IOException {

		return new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					
					MyWriter writer = new MyWriter(output, "UTF-8") ;
					
					SearchRequest irequest = iresponse.request();

					JsonObject result = new JsonObject();
					result.add("request", XMLToJSON.toJSONObject(irequest.toXML().toString()).asJsonObject("request"));
					result.add("response", XMLToJSON.toJSONObject(iresponse.toXML().toString()).asJsonObject("response"));
					toJSON(iresponse.getDocument(), result);

					JsonObject root = new JsonObject();
					root.add("result", result);

					new Gson().toJson(root, writer);
				} catch (SQLException e) {
					throw new IOException(e);
				}
			}
		};
	}

	private JsonObject toJSON(List<ReadDocument> docs, JsonObject parent) throws SQLException {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (ReadDocument doc : docs) {
			Map<String, Object> node = new HashMap<String, Object>();
			List<IndexableField> fields = doc.fields();
			for (IndexableField field : fields) {
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

}