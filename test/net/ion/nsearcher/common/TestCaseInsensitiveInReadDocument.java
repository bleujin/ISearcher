package net.ion.nsearcher.common;

import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.ArrayUtil;

import org.apache.lucene.index.IndexableField;

public class TestCaseInsensitiveInReadDocument extends TestCase {

	private ReadDocument rdoc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		WriteDocument wdoc = WriteDocument.testDocument().unknown("NAME", "bleujin").unknown("Age", 20) ;
		this.rdoc = ReadDocument.loadDocument(wdoc.toLuceneDoc(FieldIndexingStrategy.DEFAULT));
	}
	
	public void testGet() throws Exception {
		assertEquals("bleujin", rdoc.get("name")) ;
		assertEquals("20", rdoc.get("age")) ;

		assertEquals("bleujin", rdoc.get("Name")) ;
		assertEquals("20", rdoc.get("AGE")) ;
	}
	
	public void testGetField() throws Exception {
		assertEquals("bleujin", rdoc.getField("name").stringValue()) ;
		assertEquals("20", rdoc.getField("age").stringValue()) ;

		assertEquals("bleujin", rdoc.getField("Name").stringValue()) ;
		assertEquals("20", rdoc.getField("AGE").stringValue()) ;
	}
	
	public void testReserved() throws Exception {
		try {
			rdoc.reserved("name") ;
			fail() ;
		} catch(IllegalArgumentException expect){} ;

		try {
			rdoc.get(IKeywordField.ISKey) ;
			fail() ;
		} catch(IllegalArgumentException expect){} ;
	}
	
	public void testBodyValue() throws Exception {
		assertEquals(true, rdoc.bodyValue() != null) ;
	}
	
	public void testGetAsLong() throws Exception {
		assertEquals(20L, rdoc.getAsLong("AGE")) ;
		assertEquals(20L, rdoc.getAsLong("age")) ;
	}
	
	public void testGetFieldNames() throws Exception {
		String[] fieldNames = rdoc.getFieldNames();
		
		assertEquals(2, fieldNames.length) ;
		assertEquals(true, ArrayUtil.contains(new String[]{"name", "age"}, fieldNames[0])) ;
		assertEquals(true, ArrayUtil.contains(new String[]{"name", "age"}, fieldNames[1])) ;
	}
	
	
	public void testGetFields() throws Exception {
		List<IndexableField> list = rdoc.getFields();
		
		assertEquals(2, list.size()) ;
		assertEquals(true, ArrayUtil.contains(new String[]{"name", "age"}, list.get(0).name())) ;
		assertEquals(true, ArrayUtil.contains(new String[]{"name", "age"}, list.get(1).name())) ;
	}
	
	
	
	
}
