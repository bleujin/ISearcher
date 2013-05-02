package net.ion.nsearcher.search;

import java.util.List;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;

import org.apache.lucene.search.SortField;

public class TestSort extends ISTestCase{
	
	public void testEmpty() throws Exception {
		
		SortField[] sfs = new SortExpression().parse("") ;
		
		assertEquals(1, sfs.length) ;
		assertEquals(true, sfs[0] == SortField.FIELD_SCORE) ;
	}
	
	public void testOneField() throws Exception {
		SortField[] sfs = new SortExpression().parse("name _number desc") ;
		assertEquals(1, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.DOUBLE, sfs[0].getType()) ;
	}

	
	public void testOneField2() throws Exception {
		SortField[] sfs = new SortExpression().parse("name _number") ;
		assertEquals(1, sfs.length) ;
		assertEquals(false, sfs[0].getReverse()) ;
		assertEquals(SortField.DOUBLE, sfs[0].getType()) ;
	}

	public void testOneField3() throws Exception {
		SortField[] sfs = new SortExpression().parse("name asc") ;
		assertEquals(1, sfs.length) ;
		assertEquals(false, sfs[0].getReverse()) ;
		assertEquals(SortField.STRING, sfs[0].getType()) ;
	}


	
	public void testTwoField() throws Exception {
		SortField[] sfs = new SortExpression().parse("name desc, address asc") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.STRING, sfs[0].getType()) ;

		assertEquals(false, sfs[1].getReverse()) ;
		assertEquals(SortField.STRING, sfs[1].getType()) ;

	}
	
	public void testKeyField() throws Exception {
		SortField[] sfs = new SortExpression().parse("name desc, _score") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.STRING, sfs[0].getType()) ;

		assertEquals(SortField.FIELD_SCORE, sfs[1]) ;
	}


	public void testSort() throws Exception {
		Central central = writeDocument();
		
		Searcher newSearcher = central.newSearcher() ;
		SearchResponse result = newSearcher.createRequest("(name:bleujin) AND (int:[100 TO 200])").descending("int").offset(5).find() ;
		
		List<ReadDocument> docs = result.getDocument() ;
		Integer beforeValue = 200 ; // max
		for (ReadDocument doc : docs) {
			Integer currValue = Integer.valueOf(doc.get("int")) ;
			assertEquals(true, beforeValue >= currValue) ;
			beforeValue = currValue ;
		}
	}


}
