package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.util.MyWriter;

import org.apache.lucene.index.IndexableField;
public class SearchHTMLFormater implements SearchResponseFormater{

	@Produces(MediaType.TEXT_HTML)
	public StreamingOutput outputStreaming(final SearchResponse iresponse) throws IOException {

		return new StreamingOutput(){
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				MyWriter rw = new MyWriter(output, "UTF-8") ;
				SearchRequest irequest = iresponse.request();
				List<ReadDocument> docs = iresponse.getDocument();
				rw.write("<html><head><title>HTMLFormater</title></head><body>");

				rw.write("<div id='meta_information'>");
				rw.write("SearchRequest:<br/>" + StringUtil.filterHTML(irequest.toXML().toString()) + "<br/><br/>");
				rw.write("SearchResponse:<br/>" + StringUtil.filterHTML(iresponse.toXML().toString()) + "<br/><br/>");
				rw.write("</div>");

				writeContent(rw, docs) ;
				rw.write("</body></html>");

			}
		} ;
		
	}

	public void writeContent(MyWriter rw, List<ReadDocument> docs) throws IOException {
		for (ReadDocument doc : docs) {
			rw.write("<ul style='font-size: 10pt;'><li>" + doc.idValue());
			rw.write("<ul>");
			List<IndexableField> fields = doc.fields();
			for (IndexableField field : fields) {
				rw.write("<li>" + field.name() + "[") ;
				writeFieldOption(rw, field) ;
				rw.write("] : " + field.stringValue() + "<br/>");
			}
			rw.write("</ul>");
			rw.write("</ul>");
		}
	}

	

	private void writeFieldOption(MyWriter rw, IndexableField field) {
		rw.write("Sto:", (field.fieldType().stored() ? "T" : "F"), 
				",Tok:", (field.fieldType().tokenized() ? "T" : "F"), 
				",Ind:", (field.fieldType().indexed() ? "T" : "F"));
	}

}
