package net.ion.nsearcher.common;

import junit.framework.TestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexableField;

public class TestMyDocument extends TestCase {

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
		cen.close() ;
		super.tearDown();
	}
	
	public void testNew() throws Exception {
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.fieldIndexingStrategy(FieldIndexingStrategy.DEFAULT) ;
				WriteDocument mdoc = isession.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				assertEquals("bleujin", mdoc.idValue()) ;
				assertEquals(9, mdoc.toLuceneDoc(isession).getFields().size()) ; // 1 + 2 + 3 + 2
				return null;
			}
		}) ;
		
	}
	
	public void testBodyValue() throws Exception {
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.fieldIndexingStrategy(FieldIndexingStrategy.DEFAULT) ;
				WriteDocument writedoc = isession.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				
				Document doc = writedoc.toLuceneDoc(isession) ;
				assertEquals(9, doc.getFields().size()) ; // 1 + 2 + 3 + 2 + 1
				
				assertEquals(writedoc.idValue(), doc.get(IKeywordField.ISKey)) ;
				
				ReadDocument loadDoc = ReadDocument.loadDocument(doc) ;
				assertEquals("bleujin", loadDoc.idValue()) ;
				
				assertEquals(doc.get(IKeywordField.ISALL_FIELD), loadDoc.reserved(IKeywordField.ISALL_FIELD)) ;
				
				assertEquals(doc.get(IKeywordField.ISBody), loadDoc.bodyValue()) ;
				return null;
			}
		}) ;

		
		
		
	}
	
	public void testAllSame() throws Exception {
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.fieldIndexingStrategy(FieldIndexingStrategy.DEFAULT) ;
				
				WriteDocument writeDoc = isession.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				Document doc = writeDoc.toLuceneDoc(isession) ;
				ReadDocument loadDoc = ReadDocument.loadDocument(doc) ;

				for (MyField field : writeDoc.getFields()) {
					assertEquals(field.stringValue(), loadDoc.get(field.name())) ;
				}

				for (IndexableField field : loadDoc.getFields()) {
					assertEquals(field.stringValue(), doc.get(field.name())) ;
				}
				
				return null;
			}
		}) ;

		
		
		
	}

	public void testAllSameOnIndex() throws Exception {
		Document doc = indexer.index(new IndexJob<Document>() {
			public Document handle(IndexSession isession) throws Exception {
				final WriteDocument writeDoc = isession.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				isession.insertDocument(writeDoc) ;
				return writeDoc.toLuceneDoc(isession);
			}
		}) ;
		
		ReadDocument findDoc = cen.newSearcher().createRequest("20").findOne() ;
		
		for (IndexableField field : findDoc.getFields()) {
			if (IKeywordField.TIMESTAMP.equals(field.name())) continue  ;
			assertEquals(field.stringValue(), doc.get(field.name())) ;
		}
	}
	
	
}






