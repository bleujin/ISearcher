package net.ion.nsearcher.index;

import junit.framework.TestCase;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

public class TestDocument extends TestCase {

	private Indexer indexer;
	private Central cen;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.cen = CentralConfig.newRam().build();
		this.indexer = cen.newIndexer();
	}

	@Override
	protected void tearDown() throws Exception {
		cen.close();
		super.tearDown();
	}

	public void testWriteAndRead() throws Exception {
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("doctest").keyword("int", "4").insert();
				return null;
			}
		});

		Searcher searcher = cen.newSearcher();
		assertEquals(1, searcher.search("int:4").size());
	}

	public void testReadDocField() throws Exception {
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.fieldIndexingStrategy(FieldIndexingStrategy.DEFAULT);
				WriteDocument mdoc = isession.newDocument("bleujin").text("test", "he programmer").number("age", 20);
				assertEquals("bleujin", mdoc.idValue());
				assertEquals(9, mdoc.toLuceneDoc().getFields().size()); // 1 + 2 + 3 + 2
				mdoc.insert();
				return null;
			}
		});
		
		ReadDocument rdoc = cen.newSearcher().createRequestByKey("bleujin").findOne() ;
		assertEquals(1, rdoc.fieldNames().length); // textfield not saved
		assertEquals("20", rdoc.getField("age").stringValue()) ;
	}

	
	
	public void testBodyValue() throws Exception {
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.fieldIndexingStrategy(FieldIndexingStrategy.DEFAULT);
				WriteDocument writedoc = isession.newDocument("bleujin").text("test", "he programmer").number("age", 20);

				Document doc = writedoc.toLuceneDoc();
				assertEquals(9, doc.getFields().size()); // 1 + 2 + 3 + 2 + 1

				assertEquals(writedoc.idValue(), doc.get(IKeywordField.DocKey));

				ReadDocument loadDoc = ReadDocument.loadDocument(doc);
				assertEquals("bleujin", loadDoc.idValue());

				assertEquals(doc.get(IKeywordField.ISALL_FIELD), loadDoc.reserved(IKeywordField.ISALL_FIELD));

				assertEquals(doc.get(IKeywordField.BodyHash), loadDoc.bodyValue());
				return null;
			}
		});

	}

	public void testSameReadWrite() throws Exception {
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument writeDoc = isession.newDocument("bleujin").text("test", "he programmer").number("age", 20);
				Document doc = writeDoc.toLuceneDoc();
				ReadDocument loadDoc = ReadDocument.loadDocument(doc);

				for (MyField field : writeDoc.fields()) {
					assertEquals(field.stringValue(), loadDoc.asString(field.name()));
				}

				for (IndexableField field : loadDoc.fields()) {
					assertEquals(field.stringValue(), doc.get(field.name()));
				}

				return null;
			}
		});

	}

	public void testAllSameOnIndex() throws Exception {
		Document doc = indexer.index(new IndexJob<Document>() {
			public Document handle(IndexSession isession) throws Exception {
				final WriteDocument writeDoc = isession.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				isession.insertDocument(writeDoc);
				return writeDoc.toLuceneDoc();
			}
		});

		ReadDocument findDoc = cen.newSearcher().createRequest("20").findOne();

		for (IndexableField field : findDoc.fields()) {
			assertEquals(field.stringValue(), doc.get(field.name()));
		}
	}

}
