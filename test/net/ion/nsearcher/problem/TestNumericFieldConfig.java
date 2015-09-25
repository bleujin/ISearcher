package net.ion.nsearcher.problem;

import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

public class TestNumericFieldConfig extends TestCase {

	private Central cen;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.cen = CentralConfig.newRam().build() ;
		
		cen.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				List<WriteDocument> list = ListUtil.newList();
				for (int i = 1; i <= 20; i++) {
					list.add(isession.newDocument("index_" + i).number("numno", i).number("num", i).unknown("numun", "" + i).keyword("numk", "" +i) ) ;
				}
				
				Collections.shuffle(list);
				
				for (WriteDocument wdoc : list) {
					wdoc.insert() ;
				}
				return null;
			}
		}) ;
	}
	
	public void testRangeQuery() throws Exception {
		cen.newSearcher().createRequest("num:[1 TO 10]").find().debugPrint();
		Debug.line();
		cen.newSearcher().createRequest("numno:[1 TO 10]").find().debugPrint();
	}
	
	public void testSort() throws Exception {
		cen.newSearcher().createRequest("").ascending("numno").find().debugPrint();
		Debug.line();
		cen.newSearcher().createRequest("").ascending("num").find().debugPrint();
		// cen.newSearcher().createRequest("").ascending("numk").find().debugPrint(); 
	}
	
	
	public void testSortExpression() throws Exception {
		cen.newSearcher().createRequest("").sort("numno").find().debugPrint();

		cen.newSearcher().createRequest("").sort("num asc").find().debugPrint();
	}
	
	
	
	
	
	
	
}
