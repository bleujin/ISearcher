package net.ion.bleujin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import com.google.common.base.Optional;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import junit.framework.TestCase;

public class TestIO extends TestCase {

	public void testWriteRead() throws Exception {
		File file = new File("./resource/temp/hello.txt");
		
		final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		IOUtil.write("Hello", writer) ;
		writer.close();

		String readed = IOUtil.toString(new FileInputStream(file));
		Debug.line(readed) ;
	}
	
	public void testIOManager() throws Throwable {
		IOManager im = new IOManager();
		
		Integer result = im.syncWriteRequest("./resou8rce/temp/hello.txt", new WriteJob<Integer>(){
			public Integer handle(OutputStream output) throws Exception {
				final OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
				writer.write("Hello") ;
				return 5 ;
			}}
		);
	}
	
	
	public void xtestDoubleClosed() throws Exception {
		Version version = Version.LUCENE_44;
		RAMDirectory dir = new RAMDirectory();
		IndexWriter iw = new IndexWriter(dir, new IndexWriterConfig(version, new StandardAnalyzer(version))) ;
		
		Document doc = new Document();
		doc.add(new StringField("name", "bleujin", Store.YES));
		doc.add(new IntField("age", 20, Store.YES));
		
		iw.addDocument(doc);
		iw.commit();
		IOUtil.close(dir);
		
		iw.addDocument(doc);

		
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir)) ;
		
		TopDocs find = searcher.search(new MatchAllDocsQuery(), 10);
		for(ScoreDoc sc : find.scoreDocs){
			Debug.line(sc);
		}
		
	}
	
}




class B {
	private A a = new A();
	public void callA() throws Exception {
		String rtn = a.a() ;
		for(StackTraceElement ele : Thread.currentThread().getStackTrace()) {
			Debug.line(ele) ;
		}
	}
}

class A {
	public String a(){
		return "a" ;
	}
	
}