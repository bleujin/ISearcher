package net.ion.nsearcher.index.channel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.index.channel.persistor.ObjectPersistor;
import net.ion.nsearcher.index.channel.persistor.StackFile;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.processor.StdOutProcessor;

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
		Central central = writeDocument() ;
		Searcher searcher  = central.newSearcher() ;
		searcher.addPostListener(new StdOutProcessor()) ;
		
		assertEquals(0, searcher.search("int:4").size()) ;
		assertEquals(1, searcher.search("INT:4").size()) ;
	}
	
	
	public void testToLuceneDoc() throws Exception {
		Central central = writeDocument() ;
		Searcher searcher  = central.newSearcher() ;
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search("int:3");
		MyDocument mydoc = result.getDocument().get(0) ;
		
		String expected = mydoc.get(IKeywordField.ISALL_FIELD) ;
		String actual = mydoc.toLuceneDoc().get(IKeywordField.ISALL_FIELD) ;
		Debug.debug(expected, actual) ;
	}
	
	
	public void testUnderBarDoc() throws Exception {
		Central central = writeDocument() ;
		Searcher searcher  = central.newSearcher() ;
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search("ud1:sky");
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
