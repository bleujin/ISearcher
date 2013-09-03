package net.ion.nsearcher.impl;

import junit.framework.TestCase;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

public class TestDeamonIndexer extends TestCase {

	public void testDuplicatedDocId() throws Exception {

		Central c = CentralConfig.newRam().build() ;
		Indexer indexer = c.newIndexer();

		indexer.index(new IndexJob<Void>(){
			public Void handle(IndexSession isession) throws Exception {
				for (int i : ListUtil.rangeNum(123)) {
					WriteDocument doc = isession.newDocument("test").add(MyField.keyword("test", "_" + i));
					isession.updateDocument(doc);
				}
				return null;
			}
		}) ;

		c.newSearcher().search("").debugPrint();
		assertEquals(1, c.newSearcher().search("").size());
	}

}
