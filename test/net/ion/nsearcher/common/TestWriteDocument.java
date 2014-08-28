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

	private Central cen;
	private Indexer indexer;
	private WriteDocument wdoc;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.cen = CentralConfig.newRam().build();
	 	this.indexer = cen.newIndexer();
	 	this.wdoc = indexer.index(new IndexJob<WriteDocument>() {
			@Override
			public WriteDocument handle(IndexSession isession) throws Exception {
				return isession.newDocument().unknown("name", "bleujin").unknown("age", 20);
			}
		}) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		cen.close() ;
		super.tearDown();
	}
	
	
	public void testNameCase() throws Exception {
		assertEquals("bleujin", wdoc.get("name"));
	}
	
	
	public void testNameCaseInReadDoc() throws Exception {
		ReadDocument rdoc = indexer.index(new IndexJob<ReadDocument>() {
			@Override
			public ReadDocument handle(IndexSession isession) throws Exception {
				return ReadDocument.loadDocument(wdoc.toLuceneDoc());
			}
		}) ;
		assertEquals("bleujin", rdoc.asString("name")) ;
	}
	
	public void testGetField() throws Exception {
		MyField field = wdoc.myField("name");
		assertEquals("bleujin", field.stringValue()) ;
	}
	
	public void testDuplName() throws Exception {
		wdoc.unknown("name", "hero") ;
		
		assertEquals(2, wdoc.getFields("name").size()) ;
		assertEquals(0, wdoc.getFields("noname").size()) ;
		
	 	assertEquals(3, wdoc.getFields().size()) ;
	 	
	 	assertEquals("bleujin", wdoc.myField("name").stringValue()) ;
	 	
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
		final MyField field = MyField.keyword("name", "hero");
		assertEquals("name", field.name()) ;

		wdoc.add(field) ;
		assertEquals(2, wdoc.getFields("name").size()) ;
	}
	
	
	public void testReserved() throws Exception {
		Document doc = indexer.index(new IndexJob<Document>() {
			public Document handle(IndexSession isession) throws Exception {
				final WriteDocument writeDoc = isession.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				isession.insertDocument(writeDoc) ;
				return writeDoc.toLuceneDoc();
			}
		}) ;
		
		for(String keyword : IKeywordField.KEYWORD_MANDATORY_FIELD) {
			assertEquals(true, doc.get(keyword) != null) ;
		}
		
		Searcher searcher = cen.newSearcher();
		ReadDocument rd = searcher.createRequest("").findOne() ;

		
		assertEquals(true, rd.reserved(IKeywordField.DocKey) != null) ;
		assertEquals(true, rd.reserved(IKeywordField.BodyHash) == null) ;
		assertEquals(true, rd.reserved(IKeywordField.TIMESTAMP) == null) ;
	}
	
	public void testId() throws Exception {
		assertEquals(Boolean.FALSE, indexer.index(new IndexJob<Boolean>() {
			@Override
			public Boolean handle(IndexSession isession) throws Exception {
				// TODO Auto-generated method stub
				return isession.newDocument().equals(isession.newDocument());
			}
		})) ;
	}
	
	
}
