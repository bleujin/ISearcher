package net.ion.crawler.lucene;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import net.ion.crawler.event.ParserEvent;
import net.ion.crawler.link.Link;
import net.ion.crawler.parser.PageData;
import net.ion.framework.rope.RopeReader;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.index.event.IParserEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;


public abstract class AbstractLuceneParserEventListener implements IParserEventListener {

	private static final transient Log LOG = LogFactory.getLog(AbstractLuceneParserEventListener.class);

	private IndexWriter writer;


	public AbstractLuceneParserEventListener(IndexWriter writer) {
		this.writer = writer;
	}

	public void parsed(ParserEvent event) {
		PageData pageData = event.getPageData();
		String path = pageData.getLink().getURI();

		// create temporary file and index it
		// write data to temporary file
		Object data = pageData.getData();
		if (data instanceof CharSequence) {
			// FileUtil.save(file, (String) data, ((PageDataHttpClient) pageData).getCharSet(),
			// ((PageDataHttpClient) pageData).getLastModified());
			try {
				indexFile(new RopeReader((CharSequence) data), pageData.getLink());
			} catch (IOException ignore) {
				LOG.info(ignore);
			}
			// FileUtil.save(file, (String) data, null, pageData.getLink().getTimestamp());
		} else {
			Debug.warn("Page data has to be stored as a string object. link=" + path);
		}
	}

	private void indexFile(Reader reader, Link link) throws IOException {
		// parse HTML document
		final Document doc = createDocument(reader, link);
		if (doc != null) {
			// add doc unconditionally
			writer.addDocument(doc);
		}
	}

	public abstract Document createDocument(InputStream input, Link link) throws IOException;

	public abstract Document createDocument(Reader reader, Link link) throws IOException;

}
