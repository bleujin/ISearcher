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
		MyDocument mdoc = MyDocument.newDocument("bleujin").addUnknown("test", "he programmer").addUnknown("age", 20);
		assertEquals("bleujin", mdoc.idValue()) ;
		assertEquals(true, mdoc.bodyValue() != null) ;
		assertEquals(9, mdoc.toLuceneDoc().getFields().size()) ; // 1 + 2 + 3 + 2
	}
	
	public void testBodyValue() throws Exception {
		MyDocument mdoc = MyDocument.newDocument("bleujin").addUnknown("test", "he programmer").addUnknown("age", 20);
		Document doc = mdoc.toLuceneDoc() ;
		assertEquals(9, doc.getFields().size()) ; // 1 + 2 + 3 + 2 + 1
		
		assertEquals(mdoc.docId(), doc.get(IKeywordField.ISKey)) ;
		
		MyDocument loadDoc = MyDocument.loadDocument(doc) ;
		assertEquals("bleujin", loadDoc.idValue()) ;
		
		assertEquals(mdoc.get(IKeywordField.ISALL_FIELD), loadDoc.get(IKeywordField.ISALL_FIELD)) ;
		
		assertEquals(mdoc.bodyValue(), loadDoc.bodyValue()) ;
	}
	
	public void testAllSame() throws Exception {
		MyDocument mdoc = MyDocument.newDocument("bleujin").addUnknown("test", "he programmer").addUnknown("age", 20);
		Document doc = mdoc.toLuceneDoc() ;

		MyDocument loadDoc = MyDocument.loadDocument(doc) ;

		for (Fieldable field : mdoc.getFields()) {
			assertEquals(field.stringValue(), loadDoc.get(field.name())) ;
		}

		for (Fieldable field : loadDoc.getFields()) {
			assertEquals(field.stringValue(), mdoc.get(field.name())) ;
		}
	}

	public void testAllSameOnIndex() throws Exception {
		final MyDocument mdoc = MyDocument.newDocument("bleujin").addUnknown("test", "he programmer").addUnknown("age", 20);
		Central cen = CentralConfig.newRam().build();
		
		Indexer indexer = cen.newIndexer();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.insertDocument(mdoc) ;
				return null;
			}
		}) ;
		
		MyDocument findDoc = cen.newSearcher().createRequest("20").findOne() ;
		
		for (Fieldable field : findDoc.getFields()) {
			assertEquals(field.stringValue(), mdoc.get(field.name())) ;
		}
	}

}






