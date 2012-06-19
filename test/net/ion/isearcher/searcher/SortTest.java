package net.ion.isearcher.searcher;

import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.util.SortExpression;

import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;

public class SortTest extends ISTestCase{
	
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
		Directory dir = writeDocument();

		Central central = Central.createOrGet(dir) ;
		
		ISearcher newSearcher = central.newSearcher() ;
		ISearchRequest request = SearchRequest.test("(name:bleujin) AND (int:[100 TO 200])", "int desc") ;
		request.setPage(Page.create(5, 1)) ;
		SearchResponse result = newSearcher.search(request) ;
		
		List<MyDocument> docs = result.getDocument() ;
		Integer beforeValue = 200 ; // max
		for (MyDocument doc : docs) {
			Integer currValue = Integer.valueOf(doc.get("int")) ;
			Debug.debug(beforeValue, currValue) ;
			assertEquals(true, beforeValue >= currValue) ;
			beforeValue = currValue ;
		}
	}


}
