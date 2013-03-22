package net.ion.nsearcher.search;

import java.util.Collections;
import java.util.List;

import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import junit.framework.TestCase;

public class TestSearcherPaging extends TestCase {

	public void testSkip() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		
		cen.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				List<MyDocument> docs = ListUtil.newList();
				for (int i : ListUtil.rangeNum(100)) {
					docs.add(MyDocument.testDocument().addUnknown("idx", i).addUnknown("name", "bleujin"));
				}
				Collections.shuffle(docs) ;

				for (MyDocument doc : docs) {
					session.insertDocument(doc) ;
				}
				
				return null;
			}
		}) ;
		SearchResponse response = cen.newSearcher().createRequest("bleujin").descending("idx").skip(4).offset(3).find();
		assertEquals(3, response.size()) ;
		assertEquals(100, response.totalCount()) ;
		List<MyDocument> list = response.getDocument();
		
		assertEquals("95", list.get(0).get("idx")) ;
		assertEquals("94", list.get(1).get("idx")) ;
		assertEquals("93", list.get(2).get("idx")) ;
		
		
	}
	
}
