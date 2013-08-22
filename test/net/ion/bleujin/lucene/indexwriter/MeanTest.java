package net.ion.bleujin.lucene.indexwriter;

import junit.framework.TestCase;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class MeanTest extends TestCase{

	public void testField() throws Exception {
		Field f = new Field("name", "value", Store.YES, Index.ANALYZED) ;
		
		
		assertEquals(true, 0.99 < f.boost()) ;
		assertEquals(true, 1.01 > f.boost()) ;
	}
}
