package net.ion.nsearcher.search;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.Searcher;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.Filter;

public class FilterTest extends ISTestCase {

	public void testTermFilterAtRequest() throws Exception {
		Central center = writeDocument();

		// no filter
		Searcher newSearcher = center.newSearcher();

		SearchResponse result = newSearcher.search(SearchRequest.create("novision").offset(20));
		List<MyDocument> docs = result.getDocument();
		assertEquals(6, docs.size());

		// set filter
		Filter filter = new FieldCacheTermsFilter("name", new String[] { "bleujin" });
		newSearcher.andFilter(filter);
		result = newSearcher.search(SearchRequest.create("novision").offset(20));
		docs = result.getDocument();
		assertEquals(1, docs.size());

//		Debug.debug(newSearcher.confirmFilterSet(searchRequest));

		// reset filter
		newSearcher = center.newSearcher();
		result = newSearcher.search(SearchRequest.create("novision").offset(20));
		docs = result.getDocument();
		assertEquals(6, docs.size());
	}

	public void testTermFilterAtSearcher() throws Exception {
		Central center = writeDocument();

		// no filter
		Searcher newSearcher = center.newSearcher();
		SearchRequest searchRequest = SearchRequest.create("novision");
		searchRequest.offset(20);

		SearchResponse result = newSearcher.search(searchRequest);
		List<MyDocument> docs = result.getDocument();
		assertEquals(6, docs.size());

		// set filter
		Filter filter = new FieldCacheTermsFilter("name", new String[] { "bleujin" });
		newSearcher.andFilter(filter);
		result = newSearcher.search(searchRequest);
		docs = result.getDocument();
		assertEquals(1, docs.size());

		// reset filter
		newSearcher = center.newSearcher();
		result = newSearcher.search(searchRequest);
		docs = result.getDocument();
		assertEquals(6, docs.size());
	}

	public void testReopen() throws Exception {
		final Central c = CentralConfig.newRam().build() ;
		Searcher searcher = c.newSearcher();

		new Thread() {
			public void run() {
				try {
					for (int i : ListUtil.rangeNum(10)) {
						Indexer iw = c.newIndexer();
						final int idx = i ;
						iw.index(createKoreanAnalyzer(), new IndexJob<Void>() {
							public Void handle(IndexSession session) throws Exception {
								session.insertDocument(MyDocument.testDocument().add(MyField.keyword("name", "bleujin")).add(MyField.number("index", idx))) ;
								return null;
							}
						}) ;
						Thread.sleep(50) ;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}.start();

		Thread.sleep(100) ;
		for (int i : ListUtil.rangeNum(10)) {
//			searcher.reopen() ;
			assertEquals(true, searcher.searchTest("bleujin").getTotalCount() > 0);
			Thread.sleep(50) ;
		}
	}
	
	
	
	

}
