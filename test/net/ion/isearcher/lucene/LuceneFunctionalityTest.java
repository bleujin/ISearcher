package net.ion.isearcher.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.impl.SearcherTest;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.SearchResponse;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.store.Directory;

public class LuceneFunctionalityTest extends ISTestCase {

	public void testFieldReader() throws Exception {
		Document doc = new Document();
		doc.add(new Field("abc", "v1", Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("abc", new StringReader("v2")));

		assertEquals(2, doc.getFields().size());

		Debug.debug(doc.getField("abc").stringValue(), doc.getField("abc").readerValue());
	}

	public void testDupField() throws Exception {
		Directory dir = writeDocument();

		ISearcher newSearcher = new SearcherTest().makeSearcher(dir);
		ISearchRequest sreq = SearchRequest.test("int:3") ;
		sreq.setPage(Page.create(5, 1)) ;
		SearchResponse result = newSearcher.search(sreq);

		List<MyDocument> docs = result.getDocument();
		for (MyDocument doc : docs) {
			Debug.debug(doc.get("name"), doc.get("subject"), doc.getFields().size(), result.getTotalCount());
		}
	}
	

	public void testCompress() throws Exception {
		File file = new File("./data/longstring.js");
		byte[] compressByte = CompressionTools.compressString(IOUtils.toString(new FileInputStream(file)));
		Debug.debug(file.length(), compressByte.length);

		Debug.debug(CompressionTools.decompressString(compressByte).length());
	}
	
	public void testWeight() throws Exception {
		Directory dir = writeDocument();
		Central c = Central.createOrGet(dir) ;
		
		ISearcher s = c.newSearcher() ;
		
		
	}

}
