package net.ion.isearcher.crawler.lucene;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

import net.ion.framework.parse.html.GeneralParser;
import net.ion.framework.parse.html.HTag;
import net.ion.framework.parse.html.NotFoundTagException;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Tidy;

public class HTMLDocument {
	private Element rawDoc;

	public HTMLDocument(File file) throws IOException {
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		org.w3c.dom.Document root = tidy.parseDOM(new FileInputStream(file), null);
		rawDoc = root.getDocumentElement();
	}

	public HTMLDocument(InputStream is) {
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		org.w3c.dom.Document root = tidy.parseDOM(is, null);
		rawDoc = root.getDocumentElement();
	}

	public static org.apache.lucene.document.Document getDocument(InputStream is) {
		HTMLDocument htmlDoc = new HTMLDocument(is);
		org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();

		luceneDoc.add(new Field("title", htmlDoc.getTitle(), Field.Store.YES, Index.ANALYZED));
		luceneDoc.add(new Field("contents", htmlDoc.getBody(), Field.Store.YES, Index.ANALYZED));

		return luceneDoc;
	}

	public static org.apache.lucene.document.Document getDocument(Reader reader) throws IOException {
		// TODO ������..
		HTag tag = GeneralParser.parseHTML(reader);
		org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
		try {
			luceneDoc.add(new Field("title", tag.getChild("head/title").getOnlyText(), Field.Store.YES, Index.ANALYZED));
			luceneDoc.add(new Field("contents", tag.getChild("body").getOnlyText(), Field.Store.YES, Index.ANALYZED));
		} catch (NotFoundTagException ignore) {
			// throw ignore ;
		}

		return luceneDoc;
	}

	public static org.apache.lucene.document.Document Document(File file) throws IOException {
		HTMLDocument htmlDoc = new HTMLDocument(file);
		org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();

		luceneDoc.add(new Field("title", htmlDoc.getTitle(), Field.Store.YES, Index.ANALYZED));
		luceneDoc.add(new Field("contents", htmlDoc.getBody(), Field.Store.YES, Index.ANALYZED));

		String contents = null;
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringWriter sw = new StringWriter();
		String line = br.readLine();
		while (line != null) {
			sw.write(line);
			line = br.readLine();
		}
		br.close();
		contents = sw.toString();
		sw.close();

		luceneDoc.add(new Field("rawcontents", contents, Field.Store.YES, Field.Index.NO));

		return luceneDoc;
	}

	public static void main(String args[]) throws Exception {
		// HtmlDocument doc = new HtmlDocument(new File(args[0]));
		// System.out.println("Title = " + doc.getTitle());
		// System.out.println("Body  = " + doc.getBody());

		HTMLDocument doc = new HTMLDocument(new FileInputStream(new File(args[0])));
		System.out.println("Title = " + doc.getTitle());
		System.out.println("Body  = " + doc.getBody());
	}

	public String getTitle() {
		if (rawDoc == null) {
			return null;
		}

		String title = "";

		NodeList nl = rawDoc.getElementsByTagName("title");
		if (nl.getLength() > 0) {
			Element titleElement = ((Element) nl.item(0));
			Text text = (Text) titleElement.getFirstChild();
			if (text != null) {
				title = text.getData();
			}
		}
		return title;
	}

	public String getBody() {
		if (rawDoc == null) {
			return null;
		}

		String body = "";
		NodeList nl = rawDoc.getElementsByTagName("body");
		if (nl.getLength() > 0) {
			body = getBodyText(nl.item(0));
		}
		return body;
	}

	private String getBodyText(Node node) {
		NodeList nl = node.getChildNodes();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);
			switch (child.getNodeType()) {
			case Node.ELEMENT_NODE:
				buffer.append(getBodyText(child));
				buffer.append(" ");
				break;
			case Node.TEXT_NODE:
				buffer.append(((Text) child).getData());
				break;
			}
		}
		return buffer.toString();
	}

}
