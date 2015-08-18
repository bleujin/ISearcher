package net.ion.bleujin.lucene.indexwriter;

import java.io.IOException;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

public class TestMergeDocument extends TestCase{
	
	public void testMerge() throws Exception {
		RAMDirectory ramd = new RAMDirectory() ;
		
		index(ramd);
		
		List<Document> moddoc = search(ramd) ;
		
		modDoc(ramd, moddoc); 
		

		DirectoryReader dreader = DirectoryReader.open(ramd) ;
		dreader = ObjectUtil.coalesce(dreader.openIfChanged(dreader), dreader) ;
		IndexSearcher searcher = new IndexSearcher(dreader) ;
		
		TopDocs topdocs = searcher.search(new TermQuery(new Term("explain", "hello")), 10);
//		TopDocs topdocs = searcher.search(new TermQuery(new Term("age", "20")), 10);
//		TopDocs topdocs = searcher.search(NumericRangeQuery.newLongRange("age", 20L, 30L, true, true), 10);
		ScoreDoc[] sdoc = topdocs.scoreDocs ;
		
		for (int i = 0; i < sdoc.length; i++) {
			Document fdoc = dreader.document(sdoc[i].doc) ;
			Debug.line(fdoc);
		}
	}

	private void modDoc(RAMDirectory ramd, List<Document> moddoc) throws IOException {
		IndexWriter iwriter = new IndexWriter(ramd, new IndexWriterConfig(Version.LATEST, new StandardAnalyzer()));
		for (Document doc : moddoc) {
			iwriter.updateDocument(new Term("name", "bleujin"), doc);
		}
		iwriter.commit();
	}

	private List<Document> search(RAMDirectory ramd) throws IOException {
		DirectoryReader dreader = DirectoryReader.open(ramd) ;
		dreader = ObjectUtil.coalesce(dreader.openIfChanged(dreader), dreader) ;
		IndexSearcher searcher = new IndexSearcher(dreader) ;
		
		TopDocs topdocs = searcher.search(new MatchAllDocsQuery(), 10);
		ScoreDoc[] sdoc = topdocs.scoreDocs ;
		
		List<Document> result = ListUtil.newList() ;
		for (int i = 0; i < sdoc.length; i++) {
			Document fdoc = dreader.document(sdoc[i].doc) ;
			fdoc.add(new StringField("address", "seoul", Store.YES));
			result.add(fdoc) ;
		}
		return result ;
	}

	private IndexWriter index(RAMDirectory ramd) throws IOException {
		IndexWriterConfig iwconfig = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
		IndexWriter iwriter = new IndexWriter(ramd, iwconfig ) ;
		
		Document doc = new Document() ;
		doc.add(new StringField("name", "bleujin", Store.YES));
		doc.add(new StringField("nick", "bleujin", Store.YES));
		doc.add(new TextField("explain", "hello bleujin", Store.YES));
		doc.add(new LongField("age", 20, Store.YES));
		
		iwriter.addDocument(doc);
		iwriter.commit();
		iwriter.close(); 
		return iwriter;
	}

	
	
	
	
	
}
