package net.ion.bleujin.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.Searcher;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class LuceneFunctionalityTest extends ISTestCase {

	public void testFieldReader() throws Exception {
		Document doc = new Document();
		doc.add(new Field("abc", "v1", Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("abc", new StringReader("v2")));

		assertEquals(2, doc.getFields().size());

		Debug.debug(doc.getField("abc").stringValue(), doc.getField("abc").readerValue());
	}

	public void testDupField() throws Exception {
		Central cen = writeDocument();

		Searcher newSearcher = cen.newSearcher() ;
		SearchResponse result = newSearcher.createRequest("int:3").offset(5).find() ;

		List<ReadDocument> docs = result.getDocument();
		for (ReadDocument doc : docs) {
			Debug.debug(doc.get("name"), doc.get("subject"), doc.getFields().size(), result.size());
		}
	}
	

	public void testCompress() throws Exception {
		File file = new File("./data/longstring.js");
		byte[] compressByte = CompressionTools.compressString(IOUtils.toString(new FileInputStream(file)));
		Debug.debug(file.length(), compressByte.length);

		Debug.debug(CompressionTools.decompressString(compressByte).length());
	}
	
	public void testWeight() throws Exception {
		Central c = writeDocument();
		
		Searcher s = c.newSearcher() ;
	}

}
