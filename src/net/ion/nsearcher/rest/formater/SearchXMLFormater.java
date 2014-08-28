package net.ion.nsearcher.rest.formater;

import java.io.IOException;
import java.util.List;

import net.ion.framework.rest.RopeRepresentation;
import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeWriter;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;

import org.apache.ecs.xml.XML;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexableField;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public class SearchXMLFormater extends AbstractDocumentFormater implements SearchResponseFormater {
	public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";

	public Representation toRepresentation(SearchResponse iresponse) throws IOException {

		SearchRequest irequest = iresponse.request();
		RopeWriter rw = toXMLString(irequest, iresponse);

		Representation result = new RopeRepresentation(rw.getRope(), MediaType.APPLICATION_XML);
		result.setCharacterSet(CharacterSet.UTF_8);
		return result;
	}

	private RopeWriter toXMLString(SearchRequest irequest, SearchResponse iresponse) throws CorruptIndexException, IOException {

		XML result = new XML("result");

		result.addElement(irequest.toXML());
		result.addElement(iresponse.toXML());
		XML nodes = new XML("nodes");
		appendChild(nodes, iresponse.getDocument());
		result.addElement(nodes);

		RopeWriter rw = new RopeWriter();
		rw.write(XML_HEADER);
		result.output(rw);
		return rw;
	}

	private void appendChild(XML nodes, List<ReadDocument> docs) throws IOException {

		for (ReadDocument doc : docs) {
			XML node = new XML("node");
			List<IndexableField> fields = doc.fields();
			for (IndexableField field : fields) {
				XML property = new XML("property");
				property.addAttribute("name", field.name());
				property.addAttribute("stored", field.fieldType().stored());
				property.addAttribute("tokenized", field.fieldType().tokenized());
				property.addAttribute("indexed", field.fieldType().indexed());
				property.addElement("<![CDATA[" + field.stringValue() + "]]>");
				node.addElement(property);
			}
			nodes.addElement(node);
		}

	}

	public Rope toRope(List<ReadDocument> docs) throws IOException {
		XML result = new XML("result");

		XML nodes = new XML("nodes");
		result.addElement(new XML("request"));
		result.addElement(new XML("response"));
		appendChild(nodes, docs);
		result.addElement("nodes", nodes);

		RopeWriter rw = new RopeWriter();
		rw.write(XML_HEADER);
		result.output(rw);
		return rw.getRope();

	}

	public MediaType getMediaType() {
		return MediaType.APPLICATION_XML;
	}
}
