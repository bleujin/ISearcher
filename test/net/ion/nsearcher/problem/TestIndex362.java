package net.ion.nsearcher.problem;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import org.apache.lucene.analysis.ko.DStandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class TestIndex362 extends TestCase {
	
	
	public void testCreate() throws Exception {
		Directory dir = new RAMDirectory() ;
		
		Version version = Version.LUCENE_CURRENT;
		IndexWriterConfig wconfig = new IndexWriterConfig(version, new DStandardAnalyzer(version));
		IndexWriter writer = new IndexWriter(dir, wconfig);
		Document doc1 = new Document() ;
//		doc2.add(new TextField("index", "7756", Store.YES)) ;
		doc1.add(text("name", "알리안츠Best중소형 증권 투자신탁[주]")) ;
		doc1.add(text("name", "LG U+")) ;
//		doc2.add(new TextField("name", "미래에셋ASEAN업종대표증권자투자신탁 1(주식)종류A", Store.YES)) ;
//		doc2.add(new TextField("name", "필요가 없다 正道", Store.YES)) ;
//		doc2.add(new TextField("name", "한요가", Store.YES)) ;
		writer.addDocument(doc1) ;


		Document doc2 = new Document() ;
		doc2.add(text("name2", "서울E플러스 펀드"));
		doc2.add(text("name", "태극기  바람에 펄럽입니다."));
//		doc2.add(new TextField("name", "SCH-B500 1(주식)종류A ", Store.YES));
//		doc2.add(new TextField("name", "2000년 9월 30일 그 일이 일어났다. ", Store.YES));
//		doc2.add(new TextField("name", "4.19의거 발생일 ", Store.YES));
//		doc2.add(new TextField("name", "급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다", Store.YES));
		writer.addDocument(doc2);

		writer.commit() ;
		writer.close() ;
		
		
		IndexReader dreader = IndexReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(dreader);
		
		Query query = new QueryParser(version, "name", new DStandardAnalyzer(version)).parse("태극기");
		TopDocs topdocs = searcher.search(query, 10);
		
		Debug.line(topdocs.totalHits) ;
		
		dir.close() ;
	}
	
	private Field text(String name, String value){
		return new Field(name, value, Store.YES, Index.ANALYZED) ;
	}
}
