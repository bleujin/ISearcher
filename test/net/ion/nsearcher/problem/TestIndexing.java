package net.ion.nsearcher.problem;

import java.io.StringReader;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class TestIndexing extends TestCase {

	public void testCreate() throws Exception {
		Directory dir = new RAMDirectory() ;
		
		final Version version = Version.LUCENE_CURRENT;
		final Analyzer indexAnal = new CJKAnalyzer(version);
		IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(version, indexAnal) );

		Document doc1 = new Document() ;
//		doc2.add(new TextField("index", "7756", Store.YES)) ;
		doc1.add(text("name", "알리안츠Best중소형 증권 투자신탁[주]")) ;
//		doc1.add(text("name", "LG U+")) ;
//		doc2.add(new TextField("name", "미래에셋ASEAN업종대표증권자투자신탁 1(주식)종류A", Store.YES)) ;
//		doc2.add(new TextField("name", "필요가 없다 正道", Store.YES)) ;
//		doc2.add(new TextField("name", "한요가", Store.YES)) ;
		writer.addDocument(doc1) ;


		Document doc2 = new Document() ;
		doc2.add(text("name", "서울 E플러스 펀드"));
		doc2.add(text("name", "태극기  바람에 펄럽입니다."));
//		doc2.add(new TextField("name", "SCH-B500 1(주식)종류A ", Store.YES));
//		doc2.add(new TextField("name", "2000년 9월 30일 그 일이 일어났다. ", Store.YES));
//		doc2.add(new TextField("name", "4.19의거 발생일 ", Store.YES));
//		doc2.add(new TextField("name", "급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다", Store.YES));
		writer.addDocument(doc2);


		writer.commit() ;
		writer.close() ;
		
		
		DirectoryReader dreader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(dreader);
		
		final Analyzer searchAnal = new CJKAnalyzer(version);
		Debug.line(searcher.search(new QueryParser(version, "name", searchAnal).parse("증권"), 10).totalHits);
		Debug.line(searcher.search(new QueryParser(version, "name", searchAnal).parse("태극기"), 10).totalHits);
		
		dir.close() ;
	}
	
	private TextField text(String name, String value){
		return new TextField(name, new StringReader(value)) ;
	}
	
	
	public void testField() throws Exception {
		Central central = CentralConfig.newRam().build() ;
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("bleujin").add(MyField.noIndex("content", "helloworld")).update() ;
				return null;
			}
		}) ;

		Debug.line(central.newSearcher().createRequest("").findOne().getField("content")) ;
	}
	
}
