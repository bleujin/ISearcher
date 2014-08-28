package net.ion.nsearcher.common;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.ArrayUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.lucene.index.IndexableField;

public class TestReadDocument extends TestCase {

	private ReadDocument rdoc;
	private Central cen;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.cen = CentralConfig.newRam().build();
	 	Indexer indexer = cen.newIndexer();
	 	this.rdoc = indexer.index(new IndexJob<ReadDocument>() {
			@Override
			public ReadDocument handle(IndexSession isession) throws Exception {
				return ReadDocument.loadDocument(isession.newDocument("newdoc").unknown("name", "bleujin").unknown("age", 20).toLuceneDoc());
			}
		}) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		cen.close() ;
		super.tearDown();
	}
	
	public void testAsString() throws Exception {
		assertEquals("bleujin", rdoc.asString("name")) ;
		assertEquals("20", rdoc.asString("age")) ;
	}
	
	public void testField() throws Exception {
		assertEquals("bleujin", rdoc.getField("name").stringValue()) ;
		assertEquals("20", rdoc.getField("age").stringValue()) ;
	}
	
	public void testReserved() throws Exception {
		assertEquals("newdoc", rdoc.reserved(IKeywordField.DocKey));
		assertEquals(true, rdoc.reserved(IKeywordField.BodyHash) != null);
		assertEquals(true, rdoc.reserved(IKeywordField.TIMESTAMP) != null);

		assertEquals(rdoc.bodyValue(), rdoc.reserved(IKeywordField.BodyHash)) ;
		assertEquals(true, rdoc.timestamp() <= new Date().getTime());
	}
	
	public void testAsLong() throws Exception {
		assertEquals(20L, rdoc.asLong("age", 0)) ;
	}
	
	public void testFieldNames() throws Exception {
		String[] fieldNames = rdoc.fieldNames();
		
		assertEquals(2, fieldNames.length) ;
		assertEquals(true, ArrayUtil.contains(new String[]{"name", "age"}, fieldNames[0])) ;
		assertEquals(true, ArrayUtil.contains(new String[]{"name", "age"}, fieldNames[1])) ;
	}
	
	
	public void testFields() throws Exception {
		List<IndexableField> list = rdoc.fields();
		
		assertEquals(2, list.size()) ;
		assertEquals(true, ArrayUtil.contains(new String[]{"name", "age"}, list.get(0).name())) ;
		assertEquals(true, ArrayUtil.contains(new String[]{"name", "age"}, list.get(1).name())) ;
	}
	
	
	
	
}
