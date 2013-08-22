package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.util.List;

import net.ion.framework.rest.RopeRepresentation;
import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeWriter;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;

import org.apache.lucene.index.IndexableField;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public class SearchHTMLFormater extends AbstractDocumentFormater implements SearchResponseFormater{

	public Representation toRepresentation(SearchResponse iresponse) throws IOException {

		SearchRequest irequest = iresponse.request();
		List<ReadDocument> docs = iresponse.getDocument();
		RopeWriter rw = new RopeWriter();
		rw.write("<html><head><title>HTMLFormater</title></head><body>");

		rw.write("<div id='meta_information'>");
		rw.write("SearchRequest:<br/>", StringUtil.filterHTML(irequest.toXML().toString()), "<br/><br/>");
		rw.write("SearchResponse:<br/>", StringUtil.filterHTML(iresponse.toXML().toString()), "<br/><br/>");
		rw.write("</div>");

		rw.write(toRope(docs)) ;
		rw.write("</body></html>");

		Representation result = new RopeRepresentation(rw.getRope(), getMediaType());
		result.setCharacterSet(CharacterSet.UTF_8);
		return result;
	}

	public Rope toRope(List<ReadDocument> docs) throws IOException {
		RopeWriter rw = new RopeWriter() ;
		for (ReadDocument doc : docs) {
			rw.write("<ul style='font-size: 10pt;'><li>", doc.idValue());
			rw.write("<ul>");
			List<IndexableField> fields = doc.getFields();
			for (IndexableField field : fields) {
				rw.write("<li>", field.name(), "[" + getFieldOption(field), "] : ", field.stringValue(), "<br/>");
			}
			rw.write("</ul>");
			rw.write("</ul>");
		}
		return rw.getRope();
	}

	

	private Rope getFieldOption(IndexableField field) {
		RopeWriter rw = new RopeWriter();
		rw.write("Sto:", (field.fieldType().stored() ? "T" : "F"), 
				",Tok:", (field.fieldType().tokenized() ? "T" : "F"), 
				",Ind:", (field.fieldType().indexed() ? "T" : "F"));
		return rw.getRope();
	}



	public MediaType getMediaType() {
		return MediaType.TEXT_HTML;
	}



}
