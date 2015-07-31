package net.ion.nsearcher.search;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import junit.framework.TestCase;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

public class TestSort extends TestCase{

	
	public void testSortInSearch() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 20; i++) {
					isession.newDocument().number("idx", i).number("val", RandomUtil.nextInt(20)).insert() ;
				}
				return null;
			}
		}) ;

		Sort sort = central.newSearcher().createRequest("").sort("val=desc").sort() ;
		SortField sfield = sort.getSort()[0];
		assertEquals(true, sfield.getType() == Type.STRING) ;
		assertEquals(true, sfield.getReverse()) ;
		assertEquals("val", sfield.getField()) ;
	}
	
	public void testDescending() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		Sort sort = central.newSearcher().createRequest("").descending("val").sort() ;
		SortField sfield = sort.getSort()[0];
		assertEquals(true, sfield.getType() == Type.STRING) ;
		assertEquals(true, sfield.getReverse()) ;
		assertEquals("val", sfield.getField()) ;

	}

	public void testAscending() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		
		Sort sort = central.newSearcher().createRequest("").ascending("val").sort() ;
		SortField sfield = sort.getSort()[0];
		assertEquals(true, sfield.getType() == Type.STRING) ;
		assertEquals(false, sfield.getReverse()) ;
		assertEquals("val", sfield.getField()) ;

	}

}
