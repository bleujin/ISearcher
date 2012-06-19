package net.ion.isearcher.searcher;

import java.io.IOException;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class FilterTest extends ISTestCase {

	public void testTermFilterAtRequest() throws Exception {
		Directory dir = writeDocument();

		Central center = Central.createOrGet(dir);

		// no filter
		ISearcher newSearcher = center.newSearcher();
		ISearchRequest searchRequest = SearchRequest.test("novision");
		searchRequest.setPage(Page.create(20, 1));

		SearchResponse result = newSearcher.search(searchRequest);
		List<MyDocument> docs = result.getDocument();
		assertEquals(6, docs.size());

		// set filter
		Filter filter = new FieldCacheTermsFilter("name", new String[] { "bleujin" });
		newSearcher.andFilter(filter);
		result = newSearcher.search(searchRequest);
		docs = result.getDocument();
		assertEquals(1, docs.size());

		Debug.debug(newSearcher.confirmFilterSet(searchRequest));

		// reset filter
		newSearcher = center.newSearcher();
		searchRequest = SearchRequest.test("novision");
		searchRequest.setPage(Page.create(20, 1));
		newSearcher = center.newSearcher();
		result = newSearcher.search(searchRequest);
		docs = result.getDocument();
		assertEquals(6, docs.size());
	}

	public void testTermFilterAtSearcher() throws Exception {
		Directory dir = writeDocument();

		Central center = Central.createOrGet(dir);

		// no filter
		ISearcher newSearcher = center.newSearcher();
		ISearchRequest searchRequest = SearchRequest.test("novision");
		searchRequest.setPage(Page.create(20, 1));

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
		Directory dir = new RAMDirectory();

		final Central c = Central.createOrGet(dir);
		ISearcher searcher = c.newSearcher();

		new Thread() {
			public void run() {
				try {
					for (int i : ListUtil.rangeNum(10)) {
						IWriter iw = c.newIndexer(new MyKoreanAnalyzer());
						iw.begin("my");
						iw.insertDocument(MyDocument.testDocument().add(MyField.keyword("name", "bleujin")).add(MyField.number("index", i))) ;
						iw.end();
						Thread.sleep(50) ;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}.start();

		Thread.sleep(200) ;
		for (int i : ListUtil.rangeNum(10)) {
			searcher.reopen() ;
			assertEquals(true, searcher.searchTest("bleujin").getTotalCount() > 0);
			Thread.sleep(50) ;
		}
	}
	
	
	
	

}
