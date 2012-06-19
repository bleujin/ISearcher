package net.ion.isearcher.searcher;


import static net.ion.isearcher.common.IKeywordField.ISALL_FIELD;

import java.util.HashSet;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.processor.StdOutProcessor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.instantiated.InstantiatedIndex;
import org.apache.lucene.store.instantiated.InstantiatedIndexReader;
import org.apache.lucene.util.Version;

public class HangulKeywordTest extends ISTestCase {

	public void testImsi() throws Exception {
		Directory dir = new RAMDirectory();
		Central c = Central.createOrGet(dir);

		String val = "abc 서울E플러스 펀드 SCH-B500 1(주식) 종류A 2000년 9월 30일 그 일이 일어났다. 4.19 의거 발생일  급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다" ;
		// String val = "서울E플러스 펀드 4.19의거 발생일  급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다" ;

		Analyzer indexAnal = new MyKoreanAnalyzer();
		IWriter writer = c.testIndexer(indexAnal);
		writer.begin("dododo");
		MyDocument doc2 = MyDocument.testDocument();
		doc2.add(MyField.text("name", val));
		writer.insertDocument(doc2);
		writer.end();

		printTerm(c.newReader().getIndexReader(), "name") ;
		
		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		ISearcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		assertEquals(1, searcher.search(SearchRequest.create("급락", null, indexAnal)).getTotalCount());
	}
	
	public void testHangulKeyword() throws Exception {
		Directory dir = new RAMDirectory();
		Central c = Central.createOrGet(dir);

		Set stopword = new HashSet();
		// Analyzer anal = KorAnalyzer.createWithStopWord(stopword) ;
		Analyzer indexAnal = new MyKoreanAnalyzer();
		Analyzer searchAnal = new MyKoreanAnalyzer() ;
//		Analyzer searchAnal = new CJKAnalyzer(Version.LUCENE_CURRENT);
		

		IWriter writer = c.testIndexer(indexAnal);
		writer.begin("my");

		MyDocument doc2 = MyDocument.testDocument();
		doc2.add(MyField.text("index", "7756"));
		doc2.add(MyField.text("name", "LG U+"));
		doc2.add(MyField.text("name", "알리안츠Best중소형증권투자신탁[주]"));
		doc2.add(MyField.text("name", "미래에셋ASEAN업종대표증권자투자신탁 1(주식)종류A "));
		doc2.add(MyField.text("name", "필요가 없다 正道"));
		doc2.add(MyField.text("name", "한요가"));
		writer.insertDocument(doc2);

		MyDocument doc1 = MyDocument.testDocument();
		doc1.add(MyField.text("name", "서울E플러스 펀드"));
		doc1.add(MyField.text("name", "SCH-B500 1(주식)종류A "));
		doc1.add(MyField.text("name", "2000년 9월 30일 그 일이 일어났다. "));
		doc1.add(MyField.text("name", "4.19의거 발생일 "));
		doc1.add(MyField.text("name", "급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다"));
		writer.insertDocument(doc1);

		writer.end();

		printTerm(c.newReader().getIndexReader(), "IS-all") ;
		
		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		ISearcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		assertEquals(1, searcher.search(SearchRequest.create("LG U+", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("2000년 9월", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("B500", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("BEST 중소형", null, searchAnal)).getTotalCount());
		
		Debug.line(SearchRequest.create("급락", null, searchAnal).getQuery()) ;
		

		assertEquals(1, searcher.search(SearchRequest.create("급락", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("조짐", null, searchAnal)).getTotalCount());

//		assertEquals(1, searcher.search(SearchRequest.create("소형", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("4.19 의거", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("正道", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("E플러스", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("name:E플러스", null, searchAnal)).getTotalCount());

		assertEquals(1, searcher.search(SearchRequest.create("index:7756", null, searchAnal)).getTotalCount());

	}
	
	
	public void testQuery() throws Exception {
		Analyzer anal = new KoreanAnalyzer();
		// Analyzer anal = new KeywordAnalyzer();
		ISearchRequest req = SearchRequest.create("E플러스", null, anal) ;
		
		Debug.debug(req.getQuery().toString()) ;
	}
	
	private void printTerm(IndexReader reader, String name) throws Exception {
		
		InstantiatedIndex iidx = new InstantiatedIndex(reader) ;
		TermEnum tenum = new InstantiatedIndexReader(iidx).terms() ;
		
		Debug.debug(reader.maxDoc()) ;
		
		while(tenum.next()){
			Term term = tenum.term();
			if (term.field().toString().equals(name))
				Debug.debug(term) ;
		}
	}
	
	public void testRange() throws Exception {
		Directory dir = new RAMDirectory();
		Central c = Central.createOrGet(dir);

		Set stopword = new HashSet();
		// Analyzer anal = KorAnalyzer.createWithStopWord(stopword) ;
		Analyzer anal = new KoreanAnalyzer();

		IWriter writer = c.testIndexer(anal);
		writer.begin("my");

		MyDocument doc2 = MyDocument.testDocument();
		doc2.add(MyField.text("n1", "30"));
		doc2.add(MyField.text("d1", "20071231"));
		doc2.add(MyField.text("t1", "20071231-0705010"));
		writer.insertDocument(doc2);

		writer.end();

		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		ISearcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		ISearchRequest req = SearchRequest.create("n1:30", null, anal);
		ISearchResponse res = searcher.search(req);
		assertEquals(1, res.getTotalCount());

		req = SearchRequest.create("d1:20071231", null, anal);
		res = searcher.search(req);
		assertEquals(1, res.getTotalCount());

		req = SearchRequest.create("n1:[29 TO 31]", null, anal);
		res = searcher.search(req);
		assertEquals(1, res.getTotalCount());

		req = SearchRequest.create("n1:[09 TO 31]", null, anal);
		res = searcher.search(req);
		assertEquals(1, res.getTotalCount());
	}

	public void testFilter() throws Exception {
		Directory dir = new RAMDirectory();
		Central c = Central.createOrGet(dir);

		Set stopword = new HashSet();
		// Analyzer anal = KorAnalyzer.createWithStopWord(stopword) ;
		Analyzer analyzer = new KoreanAnalyzer();

		IWriter writer = c.testIndexer(analyzer);
		writer.begin("my");

		MyDocument doc1 = MyDocument.testDocument();
		doc1.add(MyField.text("n1", "30"));
		doc1.add(MyField.text("key", "value"));
		writer.insertDocument(doc1);

		MyDocument doc2 = MyDocument.testDocument();
		doc2.add(MyField.text("n1", "30"));
		doc2.add(MyField.text("d1", "20071231"));
		doc2.add(MyField.text("key", "value"));
		writer.insertDocument(doc2);

		writer.end();

		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		ISearcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, ISALL_FIELD, analyzer);

		QueryWrapperFilter newFilter = new QueryWrapperFilter(parser.parse("n1:30"));
		Filter filter = newFilter;

		ISearchRequest req = SearchRequest.create("key:value", null, analyzer);
		req.setFilter(filter);

		ISearchResponse res = searcher.search(req);
		assertEquals(2, res.getTotalCount());

		newFilter = new QueryWrapperFilter(parser.parse("d1:[20071230 TO 20071231]"));
		filter = newFilter;

		req.setFilter(filter);
		res = searcher.search(req);
		assertEquals(1, res.getTotalCount());

	}

}
