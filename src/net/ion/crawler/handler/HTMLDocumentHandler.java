package net.ion.crawler.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ion.crawler.event.ParserEvent;
import net.ion.crawler.link.Link;
import net.ion.crawler.parser.PageData;
import net.ion.framework.parse.html.HTag;
import net.ion.framework.parse.html.NotFoundTagException;
import net.ion.framework.rope.RopeReader;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.index.event.CollectorEvent;
import net.ion.nsearcher.index.handler.DocumentHandler;

import org.apache.lucene.document.DateTools;

public class HTMLDocumentHandler implements DocumentHandler {

	public static final String URL = "url";
	public static final String LASTMODIFIED = "last_modified";

	public HTMLDocumentHandler() {
	}

	public MyDocument[] makeDocument(CollectorEvent _event) throws IOException {
		if (!(_event instanceof ParserEvent))
			return new MyDocument[0];

		ParserEvent event = (ParserEvent) _event;
		PageData pageData = event.getPageData();

		if (pageData.getData() != null && pageData.getData() instanceof CharSequence) {

			List<MyField> fields = makeFields(pageData) ;
			
			if (fields.size() == 0) {
				return new MyDocument[0] ;
			}
			
			Link link = pageData.getLink();
			MyDocument doc = MyDocument.newDocument(event, link.getURI());
			for (MyField field : fields) {
				doc.add(field) ;
			}
			// add the timestamp to the document
			doc.add(MyField.keyword(LASTMODIFIED, DateTools.timeToString(link.getTimestamp(), DateTools.Resolution.MILLISECOND)));
			return new MyDocument[] { doc };
		} else {
			return new MyDocument[0];
		}
	}
	
	
	private List<MyField> makeFields(PageData pageData) throws IOException {
		if (pageData.isHTMLDataType()) {
			return htmlToFields(pageData);
		} else {
			return binaryToFields(pageData) ;
		}
	}

	private List<MyField> binaryToFields(PageData pageData) {
		List<MyField> result = new ArrayList<MyField>() ;
		result.add(MyField.text(URL, pageData.getLink().getURI())) ;
		result.add(MyField.keyword("referer", pageData.getLink().getReferer())) ;
		result.add(MyField.text("content", pageData.getData().toString())) ;

		return result;
	}

	private List<MyField> htmlToFields(PageData pageData) throws IOException {
		List<MyField> result = new ArrayList<MyField>() ;

		HTag tag = HTag.createGeneral(new RopeReader((CharSequence) pageData.getData()), "html");
		if (tag.equals(HTag.EMPTY_HTAG))  return result;

		MyField url = MyField.text(URL, pageData.getLink().getURI());
		MyField title = MyField.text("title", getOnlyText(tag, "head/title"));
		MyField content = MyField.text("content", getOnlyText(tag, "body"));

		// title boost * 2
		title.setBoost(HEAD_BOOST);

		result.add(url);
		result.add(title);
		result.add(content);
		return result ;
	}

	private String getOnlyText(HTag tag, String tagPath) throws IOException{
		try {
			return tag.getChild(tagPath).getOnlyText();
		} catch(NotFoundTagException ignore) {
			return "<null/>" ;
		}
	}
}
