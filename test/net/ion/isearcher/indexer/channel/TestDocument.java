package net.ion.isearcher.indexer.channel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.impl.SearcherTest;
import net.ion.isearcher.indexer.channel.persistor.ObjectPersistor;
import net.ion.isearcher.indexer.channel.persistor.StackFile;
import net.ion.isearcher.searcher.SearchResponse;
import net.ion.isearcher.searcher.processor.StdOutProcessor;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;

public class TestDocument extends ISTestCase{
	
	private String fileName = "c:/temp/abc.ser" ;
	private int maxLength = 5;
	public void xtestDocument() throws Exception {
		MyDocument[] docs = makeTestDocument(maxLength) ;
		
		ObjectPersistor<MyDocument> persistor = new ObjectPersistor<MyDocument>() ;
		new File(fileName).delete() ;
		StackFile<MyDocument> stack = new StackFile<MyDocument>(fileName, persistor) ;

		for (int i = 0; i < maxLength; i++) {
			stack.push(docs[i]) ;
		}

		for (int i = 0; i < maxLength; i++) {
			MyDocument doc = stack.pop() ;
		}
		stack.close() ;
	}

	
	public void testCaseSensitive() throws Exception {
		Directory dir = writeDocument() ;
		ISearcher searcher  = new SearcherTest().makeSearcher(dir) ;
		searcher.addPostListener(new StdOutProcessor()) ;
		assertEquals(0, searcher.searchTest("int:4").getTotalCount()) ;
		assertEquals(1, searcher.searchTest("INT:4").getTotalCount()) ;
	}
	
	
	public void testToLuceneDoc() throws Exception {
		Directory dir = writeDocument() ;
		ISearcher searcher  = new SearcherTest().makeSearcher(dir) ;
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search(createSearchRequest("int:3"));
		MyDocument mydoc = result.getDocument().get(0) ;
		
		String expected = mydoc.get(IKeywordField.ISALL_FIELD) ;
		String actual = mydoc.toLuceneDoc().get(IKeywordField.ISALL_FIELD) ;
		Debug.debug(expected, actual) ;
	}
	
	
	public void testUnderBarDoc() throws Exception {
		Directory dir = writeDocument() ;
		ISearcher searcher  = new SearcherTest().makeSearcher(dir) ;
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search(createSearchRequest("ud1:sky"));
	}
	
	
	public void xtestSerializable() throws Exception {
		File file = new File("c:/temp/docs") ;
		MyDocument[] docs = makeTestDocument(maxLength) ;

		
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file)) ; 
		while( maxLength-- > 0) {
			output.writeObject((Serializable)docs[maxLength]) ;
		} 
		output.close() ;
	}
	

}
