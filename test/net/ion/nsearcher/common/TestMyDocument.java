package net.ion.nsearcher.common;

import java.util.List;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

public class TestMyDocument extends TestCase {

	public void testNew() throws Exception {
		WriteDocument mdoc = MyDocument.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
		assertEquals("bleujin", mdoc.idValue()) ;
		assertEquals(9, mdoc.toLuceneDoc(FieldIndexingStrategy.DEFAULT).getFields().size()) ; // 1 + 2 + 3 + 2
	}
	
	public void testBodyValue() throws Exception {
		WriteDocument writedoc = MyDocument.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
		Document doc = writedoc.toLuceneDoc(FieldIndexingStrategy.DEFAULT) ;
		assertEquals(9, doc.getFields().size()) ; // 1 + 2 + 3 + 2 + 1
		
		assertEquals(writedoc.docId(), doc.get(IKeywordField.ISKey)) ;
		
		ReadDocument loadDoc = MyDocument.loadDocument(doc) ;
		assertEquals("bleujin", loadDoc.idValue()) ;
		
		assertEquals(doc.get(IKeywordField.ISALL_FIELD), loadDoc.reserved(IKeywordField.ISALL_FIELD)) ;
		
		assertEquals(doc.get(IKeywordField.ISBody), loadDoc.bodyValue()) ;
	}
	
	public void testAllSame() throws Exception {
		WriteDocument writeDoc = MyDocument.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
		Document doc = writeDoc.toLuceneDoc(FieldIndexingStrategy.DEFAULT) ;
		ReadDocument loadDoc = MyDocument.loadDocument(doc) ;

		for (MyField field : writeDoc.getFields()) {
			assertEquals(field.stringValue(), loadDoc.get(field.name())) ;
		}

		for (Fieldable field : loadDoc.getFields()) {
			assertEquals(field.stringValue(), doc.get(field.name())) ;
		}
	}

	public void testAllSameOnIndex() throws Exception {
		Central cen = CentralConfig.newRam().build();
		
		Indexer indexer = cen.newIndexer();
		Document doc = indexer.index(new IndexJob<Document>() {
			public Document handle(IndexSession session) throws Exception {
				final WriteDocument writeDoc = MyDocument.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				session.insertDocument(writeDoc) ;
				return writeDoc.toLuceneDoc(session.fieldIndexingStrategy());
			}
		}) ;
		
		ReadDocument findDoc = cen.newSearcher().createRequest("20").findOne() ;
		
		for (Fieldable field : findDoc.getFields()) {
			if (IKeywordField.TIMESTAMP.equals(field.name())) continue  ;
			assertEquals(field.stringValue(), doc.get(field.name())) ;
		}
	}

}






