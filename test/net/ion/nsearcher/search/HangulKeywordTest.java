package net.ion.nsearcher.search;


import java.util.HashSet;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;
import net.ion.nsearcher.search.processor.StdOutProcessor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.kr.AnalyzerUtil;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.util.Version;

public class HangulKeywordTest extends ISTestCase {

	public void testImsi() throws Exception {
		Central c = CentralConfig.newRam().build() ;

		final String val = "abc 서울E플러스 펀드 SCH-B500 1(주식) 종류A 2000년 9월 30일 그 일이 일어났다. 4.19 의거 발생일  급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다" ;
		// String val = "서울E플러스 펀드 4.19의거 발생일  급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다" ;

		Indexer writer = c.newIndexer() ;
		writer.index(createKoreanAnalyzer(), new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc2 = isession.newDocument();
				doc2.add(MyField.text("name", val));
				isession.insertDocument(doc2);
				return null;
			}
		}) ;

		printTerm(createKoreanAnalyzer(), val) ;
		
		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		Searcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		assertEquals(1, searcher.createRequest("급락", createKoreanAnalyzer()).find().size());
	}
	
	public void testMyKorean() throws Exception {
		Central c = CentralConfig.newRam().build() ;

		Analyzer indexAnal = new MyKoreanAnalyzer();

		Indexer writer = c.newIndexer();
		writer.index(indexAnal, new IndexJob<Void>() {

			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc2 = isession.newDocument();
				doc2.text("index", "7756");
				doc2.text("name", "LG U+");
				doc2.text("name", "알리안츠Best중소형증권투자신탁[주]");
				doc2.text("name", "미래에셋ASEAN업종대표증권자투자신탁 1(주식)종류A ");
				doc2.text("name", "필요가 없다 正道");
				doc2.text("name", "한요가");
				isession.insertDocument(doc2);

				WriteDocument doc1 = isession.newDocument();
				doc1.text("name", "서울E플러스 펀드");
				doc1.text("name", "SCH-B500 1(주식)종류A ");
				doc1.text("name", "2000년 9월 30일 그 일이 일어났다. ");
				doc1.text("name", "4.19의거 발생일 ");
				doc1.text("name", "급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다");
				isession.insertDocument(doc1);
				return null;
			}
		}) ;
	}
	
	public void testHangulKeyword() throws Exception {
		Central c = CentralConfig.newRam().build() ;

		// Analyzer anal = KorAnalyzer.createWithStopWord(stopword) ;
		Analyzer indexAnal = new MyKoreanAnalyzer();
		Analyzer searchAnal = new MyKoreanAnalyzer() ;
//		Analyzer searchAnal = new CJKAnalyzer(Version.LUCENE_CURRENT);
		

		Indexer writer = c.newIndexer();
		writer.index(indexAnal, new IndexJob<Void>() {

			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc1 = isession.newDocument();
				doc1.text("index", "7756");
				doc1.text("name", "LG U+");
				doc1.text("name", "알리안츠Best중소형증권투자신탁[주]");
				doc1.text("name", "미래에셋ASEAN업종대표증권자투자신탁 1(주식)종류A ");
				doc1.text("name", "필요가 없다 正道");
				doc1.text("name", "한요가");
				isession.insertDocument(doc1);

				WriteDocument doc2 = isession.newDocument();
				doc2.text("name", "서울E플러스 펀드");
				doc2.text("name", "SCH-B500 1(주식)종류A ");
				doc2.text("name", "2000년 9월 30일 그 일이 일어났다. ");
				doc2.text("name", "4.19의거 발생일 ");
				doc2.text("name", "급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다");
				isession.insertDocument(doc2);
				return null;
			}
		}) ;


		
		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		Searcher searcher = c.newSearcher();
//		printTerm(indexAnal, searcher.search("").getDocument().get(1).bodyValue()) ;

		
		searcher.addPostListener(stdOutProcessor);

		assertEquals(1, searcher.createRequest("LG U+", searchAnal).find().size());

		assertEquals(1, searcher.createRequest("2000년 9월", searchAnal).find().size());

		assertEquals(1, searcher.createRequest("BEST 중소형", searchAnal).find().size());
		

		assertEquals(1, searcher.createRequest("급락", searchAnal).find().size());

		assertEquals(1, searcher.createRequest("조짐", searchAnal).find().size());

//		assertEquals(1, searcher.createRequest("소형", searchAnal)).getTotalCount());

		assertEquals(1, searcher.createRequest("4.19 의거", searchAnal).find().size());

		assertEquals(1, searcher.createRequest("正道", searchAnal).find().size());

		assertEquals(1, searcher.createRequest("E플러스", searchAnal).find().size());

		assertEquals(1, searcher.createRequest("name:E플러스", searchAnal).find().size());

		assertEquals(1, searcher.createRequest("index:7756", searchAnal).find().size());

		assertEquals(1, searcher.createRequest("B500", searchAnal).find().size());


	}
	
	
	public void testQuery() throws Exception {
		Central c = CentralConfig.newRam().build() ;
		Analyzer anal = new KoreanAnalyzer();
		// Analyzer anal = new KeywordAnalyzer();
		SearchRequest req = c.newSearcher().createRequest("E플러스", anal) ;
		
		Debug.debug(req.query().toString()) ;
	}
	
	private void printTerm(Analyzer analyzer, String source) throws Exception {
		String[] tokens = AnalyzerUtil.toToken(analyzer, source);
		
		Debug.line(tokens) ;
	}
	
	public void testRange() throws Exception {
		Central c = CentralConfig.newRam().build() ;

		Set stopword = new HashSet();
		// Analyzer anal = KorAnalyzer.createWithStopWord(stopword) ;
		Analyzer anal = new KoreanAnalyzer();

		Indexer writer = c.newIndexer();
		writer.index(anal, new IndexJob<Void>() {

			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc2 = isession.newDocument();
				doc2.add(MyField.text("n1", "30"));
				doc2.add(MyField.text("d1", "20071231"));
				doc2.add(MyField.text("t1", "20071231-0705010"));
				isession.insertDocument(doc2);
				return null;
			}
		}) ;


		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		Searcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		SearchResponse res = searcher.createRequest("n1:30", anal).find();
		assertEquals(1, res.size());

		res = searcher.createRequest("d1:20071231", anal).find();
		assertEquals(1, res.size());

		res = searcher.createRequest("n1:[29 TO 31]", anal).find();
		assertEquals(1, res.size());

		res = searcher.createRequest("n1:[29 TO 31]", anal).find();
		assertEquals(1, res.size());
	}

	public void testFilter() throws Exception {
		Central c = CentralConfig.newRam().build() ;

		Set stopword = new HashSet();
		// Analyzer anal = KorAnalyzer.createWithStopWord(stopword) ;
		Analyzer analyzer = new KoreanAnalyzer();

		Indexer writer = c.newIndexer();
		writer.index(analyzer, new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc1 = isession.newDocument();
				doc1.add(MyField.text("n1", "30"));
				doc1.add(MyField.text("key", "value"));
				isession.insertDocument(doc1);

				WriteDocument doc2 = isession.newDocument();
				doc2.add(MyField.text("n1", "30"));
				doc2.add(MyField.text("d1", "20071231"));
				doc2.add(MyField.text("key", "value"));
				isession.insertDocument(doc2);
				return null;
			}
		}) ;
		

		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		Searcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, SearchConstant.ISALL_FIELD, analyzer);

		QueryWrapperFilter newFilter = new QueryWrapperFilter(parser.parse("n1:30"));
		Filter filter = newFilter;

		SearchRequest req = searcher.createRequest("key:value", analyzer);
		req.setFilter(filter);

		SearchResponse res = searcher.search(req);
		assertEquals(2, res.size());

		newFilter = new QueryWrapperFilter(parser.parse("d1:[20071230 TO 20071231]"));
		filter = newFilter;

		req.setFilter(filter);
		res = searcher.search(req);
		assertEquals(1, res.size());

	}

}
