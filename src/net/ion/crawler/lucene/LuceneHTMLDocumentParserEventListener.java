package net.ion.crawler.lucene;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import net.ion.crawler.link.Link;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public class LuceneHTMLDocumentParserEventListener extends AbstractLuceneParserEventListener {

	private static final transient Log LOG = LogFactory.getLog(LuceneHTMLDocumentParserEventListener.class);

	public static final String FIELD_URL = "url";

	public static final String FIELD_TIMESTAMP = "timestamp";

	public LuceneHTMLDocumentParserEventListener(IndexWriter writer) {
		super(writer);
	}

	@Override
	public Document createDocument(InputStream input, Link link) throws IOException {
		// parse HTML document
		Document doc = HTMLDocument.getDocument(input);
		// add URL to the document
		doc.add(new Field(FIELD_URL, link.getURI(), Field.Store.YES, Field.Index.NO));
		// add the timestamp to the document
		doc.add(new Field(FIELD_TIMESTAMP, DateTools.timeToString(link.getTimestamp(), DateTools.Resolution.MILLISECOND), Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		return doc;
	}

	@Override
	public Document createDocument(Reader reader, Link link) throws IOException {
		// parse HTML document
		Document doc = HTMLDocument.getDocument(reader);
		// add URL to the document
		doc.add(new Field(FIELD_URL, link.getURI(), Field.Store.YES, Field.Index.NO));
		// add the timestamp to the document
		doc.add(new Field(FIELD_TIMESTAMP, DateTools.timeToString(link.getTimestamp(), DateTools.Resolution.MILLISECOND), Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		return doc;
	}

}
