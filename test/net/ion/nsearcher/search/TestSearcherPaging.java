package net.ion.nsearcher.search;

import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

public class TestSearcherPaging extends TestCase {

	public void testSkip() throws Exception {
		Central cen = CentralConfig.newRam().build();

		cen.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				List<WriteDocument> docs = ListUtil.newList();
				for (int i : ListUtil.rangeNum(100)) {
					docs.add(isession.newDocument().unknown("idx", i).unknown("name", "bleujin"));
				}
				Collections.shuffle(docs);

				for (WriteDocument doc : docs) {
					isession.insertDocument(doc);
				}

				return null;
			}
		});
		SearchResponse response = cen.newSearcher().createRequest("bleujin").descending("idx _number").skip(4).offset(3).find();
		assertEquals(3, response.size());
		assertEquals(100, response.totalCount());
		List<ReadDocument> list = response.getDocument();

		response.debugPrint();

		assertEquals("95", list.get(0).get("idx"));
		assertEquals("94", list.get(1).get("idx"));
		assertEquals("93", list.get(2).get("idx"));

		cen.close();
	}

	public void testStringOrder() throws Exception {
		Central cen = CentralConfig.newRam().build();

		cen.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				List<WriteDocument> docs = ListUtil.newList();
				docs.add(isession.newDocument().keyword("idx", "2"));
				docs.add(isession.newDocument().keyword("idx", "10"));
				docs.add(isession.newDocument().keyword("idx", "9"));
				Collections.shuffle(docs);

				for (WriteDocument doc : docs) {
					isession.insertDocument(doc);
				}
				return null;
			}
		});
		SearchResponse response = cen.newSearcher().createRequest("").descending("idx").find();
		assertEquals(3, response.size());
		List<ReadDocument> list = response.getDocument();

		response.debugPrint();

		assertEquals("9", list.get(0).get("idx"));
		assertEquals("2", list.get(1).get("idx"));
		assertEquals("10", list.get(2).get("idx"));

		cen.close();

	}

}
