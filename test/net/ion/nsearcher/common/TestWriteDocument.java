package net.ion.nsearcher.common;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.document.Document;

public class TestWriteDocument extends TestCase {

	private WriteDocument wdoc;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.wdoc = IndexSession.testDocument().unknown("Name", "bleujin").unknown("age", 20);
	}
	
	public void testNameCase() throws Exception {
		assertEquals("bleujin", wdoc.get("name"));
		assertEquals("bleujin", wdoc.myField("NAME").stringValue());
	}
	
	
	public void testNameCaseInReadDoc() throws Exception {
		WriteDocument wdoc = IndexSession.testDocument().unknown("Name", "bleujin");
		
		ReadDocument rdoc = ReadDocument.loadDocument(wdoc.toLuceneDoc(FieldIndexingStrategy.DEFAULT));
		
		assertEquals("bleujin", rdoc.get("name")) ;
		assertEquals("bleujin", rdoc.get("Name")) ;
	}
	
	public void testGetField() throws Exception {
		MyField field = wdoc.myField("NAME");
		assertEquals("bleujin", field.stringValue()) ;
	}
	
	public void testDuplName() throws Exception {
		wdoc.unknown("name", "hero") ;
		
		assertEquals(2, wdoc.getFields("name").size()) ;
		assertEquals(2, wdoc.getFields("NAME").size()) ;
		assertEquals(0, wdoc.getFields("noname").size()) ;
		
	 	assertEquals(3, wdoc.getFields().size()) ;
	 	
	 	assertEquals("bleujin", wdoc.myField("Name").stringValue()) ;
	 	
	 	Central cen = CentralConfig.newRam().build();
	 	Indexer indexer = cen.newIndexer();
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.insertDocument(wdoc) ;
				return null;
			}
		}) ;
		
		Searcher searcher = cen.newSearcher();
		
		assertEquals(1, searcher.createRequest("bleujin").find().size()) ;
		assertEquals(1, searcher.createRequest("hero").find().size()) ;
	}
	
	
	

	public void testAddField() throws Exception {
		final MyField field = MyField.keyword("NAME", "hero");
		assertEquals("name", field.name()) ;

		wdoc.add(field) ;
		assertEquals(2, wdoc.getFields("name").size()) ;
	}
	
	
	public void testReserved() throws Exception {
		Central cen = CentralConfig.newRam().build();
		
		Indexer indexer = cen.newIndexer();
		Document doc = indexer.index(new IndexJob<Document>() {
			public Document handle(IndexSession session) throws Exception {
				final WriteDocument writeDoc = session.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				session.insertDocument(writeDoc) ;
				return writeDoc.toLuceneDoc(session.fieldIndexingStrategy());
			}
		}) ;
		
		for(String keyword : IKeywordField.KEYWORD_MANDATORY_FIELD) {
			assertEquals(true, doc.get(keyword) != null) ;
		}
		
		Searcher searcher = cen.newSearcher();
		ReadDocument rd = searcher.createRequest("").findOne() ;

		for(String keyword : IKeywordField.KEYWORD_MANDATORY_FIELD) {
			assertEquals(true, rd.reserved(keyword) != null) ;
		}
	}
	
	public void testId() throws Exception {
		WriteDocument doc1 = IndexSession.testDocument();
		WriteDocument doc2 = IndexSession.testDocument();
		
		Debug.line(doc1.idValue(), doc2.idValue()) ;
	}
	
	
}
