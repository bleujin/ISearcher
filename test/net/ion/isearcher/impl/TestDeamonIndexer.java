package net.ion.isearcher.impl;

import net.ion.framework.db.Page;
import net.ion.framework.util.ListUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

public class TestDeamonIndexer extends TestCase {

	public void testDuplicatedDocId() throws Exception {

		Central c = Central.createOrGet(new RAMDirectory());
		IWriter writer = c.newDaemonIndexer(new CJKAnalyzer(Version.LUCENE_36));

		writer.begin("my");
		for (int i : ListUtil.rangeNum(1234)) {
			MyDocument doc = testDocument().add(MyField.keyword("test", "_" + i));
			writer.insertDocument(doc);
		}
		writer.end();

		c.newSearcher().searchTest("").debugPrint(Page.TEN);
		assertEquals(1, c.newSearcher().searchTest("").getTotalCount());
	}

	private static MyDocument testDocument() {
		Document doc = new Document();
		String keyString = "test";
		doc.add(new Field("IS-Key", keyString, org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.NOT_ANALYZED));
		doc.add(new Field("IS-Body", "", org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.NOT_ANALYZED));
		doc.add(new Field("IS-CollectorName", "unknown", org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.ANALYZED));
		doc.add(new Field("IS-EventType", net.ion.isearcher.events.ICollectorEvent.EventType.Normal.toString(), org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.ANALYZED));
		doc.add(new Field("timestamp", String.valueOf(System.currentTimeMillis()), org.apache.lucene.document.Field.Store.YES, org.apache.lucene.document.Field.Index.NOT_ANALYZED));
		return MyDocument.loadDocument(doc);
	}

}
