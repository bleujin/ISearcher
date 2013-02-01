package net.ion.nsearcher.impl;

import junit.framework.TestCase;
import net.ion.framework.db.Page;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.index.event.ICollectorEvent.EventType;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class TestDeamonIndexer extends TestCase {

	public void testDuplicatedDocId() throws Exception {

		Central c = CentralConfig.newRam().build() ;
		Indexer indexer = c.newIndexer();

		indexer.index(new CJKAnalyzer(SearchConstant.LuceneVersion), new IndexJob<Void>(){
			public Void handle(IndexSession session) throws Exception {
				for (int i : ListUtil.rangeNum(123)) {
					MyDocument doc = testDocument().add(MyField.keyword("test", "_" + i));
					session.updateDocument(doc);
				}
				return null;
			}
		}) ;

		c.newSearcher().search("").debugPrint();
		assertEquals(1, c.newSearcher().search("").totalCount());
	}

	private static MyDocument testDocument() {
		Document doc = new Document();
		String keyString = "test";
		doc.add(new Field(SearchConstant.ISKey, keyString, org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.NOT_ANALYZED));
		doc.add(new Field(SearchConstant.ISBody, "", org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.NOT_ANALYZED));
		doc.add(new Field(SearchConstant.ISCollectorName, "unknown", org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.ANALYZED));
		doc.add(new Field(SearchConstant.ISEventType, EventType.Normal.toString(), org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.ANALYZED));
		doc.add(new Field("timestamp", String.valueOf(System.currentTimeMillis()), org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.NOT_ANALYZED));
		return MyDocument.loadDocument(doc);
	}

}
