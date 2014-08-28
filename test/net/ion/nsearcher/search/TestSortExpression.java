package net.ion.nsearcher.search;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

import org.apache.lucene.search.SortField;

public class TestSortExpression extends ISTestCase{
	
	public void testEmpty() throws Exception {
		
		SortField[] sfs = new SortExpression().parse("") ;
		
		assertEquals(1, sfs.length) ;
		assertEquals(true, sfs[0] == SortField.FIELD_SCORE) ;
	}
	
	public void testOneField() throws Exception {
		SortField[] sfs = new SortExpression().parse("name _number desc") ;
		assertEquals(1, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.DOUBLE, sfs[0].getType()) ;
	}

	
	public void testOneField2() throws Exception {
		SortField[] sfs = new SortExpression().parse("name _number") ;
		assertEquals(1, sfs.length) ;
		assertEquals(false, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.DOUBLE, sfs[0].getType()) ;
	}

	public void testOneField3() throws Exception {
		SortField[] sfs = new SortExpression().parse("name asc") ;
		assertEquals(1, sfs.length) ;
		assertEquals(false, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[0].getType()) ;
	}


	
	public void testTwoField() throws Exception {
		SortField[] sfs = new SortExpression().parse("name desc, address asc") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[0].getType()) ;

		assertEquals(false, sfs[1].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[1].getType()) ;

	}
	
	public void testKeyField() throws Exception {
		SortField[] sfs = new SortExpression().parse("name desc, _score") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[0].getType()) ;

		assertEquals(SortField.FIELD_SCORE, sfs[1]) ;
	}
	
	public void testAtSearchRequest() throws Exception {
		Central central = sampleTestDocument();
		
		Searcher newSearcher = central.newSearcher() ;
		SearchRequest sreq = newSearcher.createRequest("(name:bleujin) AND (int:[100 TO 200])");
		
		SearchResponse result = SortExpression.applySort(sreq, "int").offset(5).find() ;

		result.debugPrint(); 
	}


	public void testSort() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					isession.newDocument().keyword("name", "bleuji").number("int", RandomUtil.nextInt(300)).update(); 
				}
				return null;
			}
		}) ;
		
		Searcher newSearcher = central.newSearcher() ;
		SearchResponse result = newSearcher.createRequest("(name:bleujin) AND (int:[100 TO 200])").descending("int").offset(5).find() ;
		
		result.debugPrint("int");
		
		
		List<ReadDocument> docs = result.getDocument() ;
		Integer beforeValue = 200 ; // max
		for (ReadDocument doc : docs) {
			Integer currValue = Integer.valueOf(doc.asString("int")) ;
			assertEquals(true, beforeValue >= currValue) ;
			beforeValue = currValue ;
		}
	}


}
