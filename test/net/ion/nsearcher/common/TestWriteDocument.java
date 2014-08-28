package net.ion.nsearcher.common;

import junit.framework.TestCase;
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
	
	
	public void tetAsString() throws Exception {
		assertEquals("bleujin", wdoc.asString("name"));
	}

	public void testFirstField() throws Exception {
		MyField field = wdoc.firstField("name");
		assertEquals("bleujin", field.stringValue()) ;
	}

	
	public void testDuplName() throws Exception {
		wdoc.unknown("name", "hero") ;
		
		assertEquals(2, wdoc.fields("name").size()) ;
		assertEquals(0, wdoc.fields("noname").size()) ;
		
	 	assertEquals(3, wdoc.fields().size()) ; // 2+1
	 	
	 	assertEquals("bleujin", wdoc.firstField("name").stringValue()) ;
	 	assertEquals("hero", wdoc.fields("name").get(1).stringValue()) ;
	 	
	 	// when save doc
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
	
	
	public void testReservedField() throws Exception {
		Document doc = indexer.index(new IndexJob<Document>() {
			public Document handle(IndexSession isession) throws Exception {
				return isession.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20).insert().toLuceneDoc() ;
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
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("newdoc").insert();
				return null ;
			}
		}) ;
		
		assertEquals(1, cen.newSearcher().createRequestByKey("newdoc").find().size()) ;
		assertEquals(0, cen.newSearcher().createRequestByKey("NEWDOC").find().size()) ;
	}
	
	
}
