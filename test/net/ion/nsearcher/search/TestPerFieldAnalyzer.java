package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.Map;

import org.apache.ecs.xhtml.ins;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.RAMDirectory;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;
import junit.framework.TestCase;

public class TestPerFieldAnalyzer extends TestCase {

	public void testWhenIndex() throws Exception {
		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("id", new KeywordAnalyzer());
		mapAnal.put("name", new CJKAnalyzer(SearchConstant.LuceneVersion));
		Analyzer anlyzer = new PerFieldAnalyzerWrapper(new MyKoreanAnalyzer(), mapAnal);
		Central central = CentralConfig.newRam().indexConfigBuilder().indexAnalyzer(anlyzer).parent().searchConfigBuilder().build();

		// Central central = CentralConfig.newRam().indexConfigBuilder().parent().searchConfigBuilder().queryAnalyzer(new SimpleAnalyzer(SearchConstant.LuceneVersion)).build() ;
		central.newIndexer().index(anlyzer, new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("perfield").add(MyField.text("id", "태극기", Store.YES)).add(MyField.text("name", "태극기", Store.YES)).update();
				return null;
			}
		});

		ReadDocument rdoc = central.newSearcher().createRequest("").findOne();
		Debug.line(rdoc.getField("id"), rdoc.getField("name"));

		Debug.debug(central.newSearcher().createRequest("id:태극기").query());
		central.newSearcher().createRequest("id:태극기").find().debugPrint(); // not found
		Debug.debug(central.newSearcher().createRequest("name:태극기").query()); 
		central.newSearcher().createRequest("name:태극기").find().debugPrint(); // found
	}


	public void testWhenSelect() throws Exception {
		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("id", new KeywordAnalyzer());
		mapAnal.put("name", new CJKAnalyzer(SearchConstant.LuceneVersion));
		Analyzer sanlyzer = new PerFieldAnalyzerWrapper(new MyKoreanAnalyzer(), mapAnal);

		Central central = CentralConfig.newRam()
					.indexConfigBuilder().indexAnalyzer(new CJKAnalyzer(SearchConstant.LuceneVersion)).parent()
					.searchConfigBuilder().queryAnalyzer(sanlyzer).build();

		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("perfield").add(MyField.text("id", "태극기", Store.YES)).add(MyField.text("name", "태극기", Store.YES)).update();
				return null;
			}
		});

		ReadDocument rdoc = central.newSearcher().createRequest("").findOne();
		assertEquals(0, central.newSearcher().createRequest("id:태극기").find().size()) ;
		assertEquals(1, central.newSearcher().createRequest("name:태극기").find().size()) ;
	}

	

	
	
	
	public void testLucene() throws Exception {
		RAMDirectory dir = new RAMDirectory();
		
		Analyzer analyzer = new StandardAnalyzer(SearchConstant.LuceneVersion);

		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("title", new KeywordAnalyzer());
		mapAnal.put("name", new CJKAnalyzer(SearchConstant.LuceneVersion));
		analyzer = new PerFieldAnalyzerWrapper(new MyKoreanAnalyzer(), mapAnal);

		
		
		IndexWriterConfig iwc = new IndexWriterConfig(SearchConstant.LuceneVersion, analyzer);
		IndexWriter w = new IndexWriter(dir, iwc);
		addDoc(w, "Lucene in Action", "193398817");
		addDoc(w, "Lucene for Dummies", "55320055Z");
		addDoc(w, "Managing Gigabytes", "55063554A");
		addDoc(w, "The Art of Computer Science", "9900333X");
		w.close();
		
		DirectoryReader reader = DirectoryReader.open(dir) ;
		IndexSearcher searcher = new IndexSearcher(reader);
		int hitsPerPage = 10;
		Query q = new QueryParser(SearchConstant.LuceneVersion, "title", new KeywordAnalyzer()).parse("title:\"Managing Gigabytes\"");
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		System.out.println("Found " + hits.length + " hits. query:" + q);
		for(int i=0;i<hits.length;++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
		}
		
	}

	private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Store.YES));
		doc.add(new StringField("isbn", isbn, Store.YES));
		w.addDocument(doc);
	}

	public void testParse() throws Exception {

		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("id", new KeywordAnalyzer());
		mapAnal.put("name", new MyKoreanAnalyzer());
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new MyKoreanAnalyzer(), mapAnal);

		analyzer = new StandardAnalyzer(SearchConstant.LuceneVersion) ;
		analyzer = new CJKAnalyzer(SearchConstant.LuceneVersion) ;
		
		TokenStream tokenStream = analyzer.tokenStream("id", "태극기가 바람에 펄럭");
		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();

		JsonArray result = new JsonArray();
		while (tokenStream.incrementToken()) {
			int startOffset = offsetAttribute.startOffset();
			int endOffset = offsetAttribute.endOffset();
			result.add(new JsonObject().put("term", charTermAttribute.toString()).put("start", startOffset).put("end", endOffset));
		}
		IOUtil.close(tokenStream);
		IOUtil.close(analyzer);
		Debug.line(result);
	}
}
