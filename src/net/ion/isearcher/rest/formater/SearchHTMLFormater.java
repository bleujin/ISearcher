package net.ion.isearcher.rest.formater;

import java.io.IOException;
import java.util.List;

import net.ion.framework.rest.RopeRepresentation;
import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeWriter;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.ISearchResponse;

import org.apache.lucene.document.Fieldable;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public class SearchHTMLFormater extends AbstractDocumentFormater implements SearchResponseFormater{

	public Representation toRepresentation(ISearchResponse iresponse) throws IOException {

		ISearchRequest irequest = iresponse.getRequest();
		List<MyDocument> docs = iresponse.getDocument();
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

	public Rope toRope(List<MyDocument> docs) throws IOException {
		RopeWriter rw = new RopeWriter() ;
		for (MyDocument doc : docs) {
			rw.write("<ul style='font-size: 10pt;'><li>", doc.getIdValue());
			rw.write("<ul>");
			List<Fieldable> fields = doc.getFields();
			for (Fieldable field : fields) {
				rw.write("<li>", field.name(), "[" + getFieldOption(field), "] : ", field.stringValue(), "<br/>");
			}
			rw.write("</ul>");
			rw.write("</ul>");
		}
		return rw.getRope();
	}

	

	private Rope getFieldOption(Fieldable field) {
		RopeWriter rw = new RopeWriter();
		rw.write("St:", (field.isStored() ? "T" : "F"), 
				",To:", (field.isTokenized() ? "T" : "F"), 
				",In:", (field.isIndexed() ? "T" : "F"), 
				",Bi:", (field.isBinary() ? "T" : "F"));
		return rw.getRope();
	}



	public MediaType getMediaType() {
		return MediaType.TEXT_HTML;
	}



}
