package net.ion.nsearcher.impl;

import java.util.Date;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import junit.framework.TestCase;

public class TestNumLoad extends TestCase {

	private Central central;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.central = CentralConfig.newLocalFile().dirFile("./resource/temp2").build() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		central.close(); 
		super.tearDown();
	}
	
	public void testSave() throws Exception {
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).stext("explain", "hello world").date("bday", new Date()).number("birth", 1975) .updateVoid() ;
				return null;
			}
		}) ;
		
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.loadDocument("bleujin").keyword("address", "seoul") .updateVoid() ;
				return null;
			}
		}) ;

		central.newSearcher().createRequest("").find().debugPrint("name", "age", "explain", "address"); 
		
		Debug.line("name");
		central.newSearcher().createRequest("name:bleujin").find().debugPrint("name", "age", "explain", "address");
		
		Debug.line("explain");
		central.newSearcher().createRequest("explain:hello").find().debugPrint("name", "age", "explain", "address");
		
		Debug.line("age");
		central.newSearcher().createRequest("age:20").find().debugPrint("name", "age", "explain", "address");

		Debug.line("address");
		central.newSearcher().createRequest("address:seoul").find().debugPrint("name", "age", "explain", "address");

		Debug.line("bday");
		central.newSearcher().createRequest("bday:20170315").find().debugPrint("name", "age", "explain", "address", "bday");


	}
	

}
