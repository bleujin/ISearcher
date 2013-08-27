package net.ion.nsearcher.search;

import java.io.IOException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.SetUtil;
import net.ion.nsearcher.common.AbDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.debug.standard.DStandardAnalyzer;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;



public class TestStopWord extends TestCase {

	
	public void testStopWord() throws Exception {
		Directory dir = new RAMDirectory() ;
		Version version = Version.LUCENE_CURRENT ;
		Analyzer indexAnal = new DStandardAnalyzer(version);
		IndexWriter iwriter = new IndexWriter(dir, new IndexWriterConfig(version, indexAnal)) ;
		iwriter.close(); 
		

		Query noStopwordquery = new QueryParser(version, "type", new DStandardAnalyzer(version)).parse("name:i-on") ;
		Query stopwordquery = new QueryParser(version, "type", new DStandardAnalyzer(version, CharArraySet.copy(version, SetUtil.create("i-on")))).parse("name:i-on") ;

		IndexReader reader = IndexReader.open(dir) ;
		
		Debug.line(stopwordquery , noStopwordquery ) ;

	}
	
	
	public void testAnalyzer() throws Exception {
		
		Directory dir = new RAMDirectory() ;
		
		Version version = Version.LUCENE_CURRENT ;
		IndexWriter iwriter = new IndexWriter(dir, new IndexWriterConfig(version, new DStandardAnalyzer(version))) ;
		writeIndex(iwriter) ;
		iwriter.close(); 
		
		
		DirectoryReader dreader = DirectoryReader.open(dir) ;
		IndexSearcher searcher = new IndexSearcher(dreader) ;
		Query query = new QueryParser(version, "type", new KoreanAnalyzer(version, SetUtil.create().toArray(new String[0]))).parse("name:i-on") ;
		
//		Debug.line(query, query.);
		
		TopDocs result = searcher.search(query, 10);
		ScoreDoc[] docs = result.scoreDocs;
		for (ScoreDoc scoreDoc : docs) {
			Document doc = dreader.document(scoreDoc.doc) ;
			Debug.line(doc);
		}
		
		Debug.line(result.totalHits, docs);
		
		IOUtil.closeSilent(dreader, dir);
	}

	
	public void testISearcher() throws Exception {
		Directory dir = new RAMDirectory() ;
		Version version = Version.LUCENE_CURRENT ;
		CharArraySet stopwords = CharArraySet.copy(version, SetUtil.create("i-on"));
		DStandardAnalyzer searchAnal = new DStandardAnalyzer(version, stopwords);
		
		Central central = CentralConfig.oldFromDir(dir).searchConfigBuilder().queryAnalyzer(searchAnal).build() ;
		
		Indexer indexer = central.newIndexer() ;
		Analyzer indexAnal = new DStandardAnalyzer(version);
		indexer.index(indexAnal, new IndexJob<Void>() {

			public Void handle(IndexSession isession) throws Exception {
				String[] array = new String[]{"hero", "bleujin", "i-on"} ;
				for (int i = 0; i < array.length; i++) {
					WriteDocument doc = isession.newDocument() ;
					doc.add(MyField.keyword("type", "sample"));
					doc.add(MyField.text("name", array[i]));
					isession.insertDocument(doc) ;
				}
				return null;
			}
		}) ;
		
		Searcher searcher = central.newSearcher() ;
		searcher.search("i-on").debugPrint();
		central.close(); 
		
		
	}
	
	
	
	private void writeIndex(IndexWriter iwriter) throws CorruptIndexException, IOException {
		String[] array = new String[]{"hero", "bleujin", "I-ON"} ;
		for (int i = 0; i < array.length; i++) {
			Document doc = new Document();
			doc.add(new Field("type", "sample", Store.YES, Index.ANALYZED));
			doc.add(new Field("name", array[i], Store.YES, Index.ANALYZED));
			iwriter.addDocument(doc);
		}
	}
}
