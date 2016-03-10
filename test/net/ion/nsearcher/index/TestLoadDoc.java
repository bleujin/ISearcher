package net.ion.nsearcher.index;

import java.io.IOException;
import java.util.Date;

import org.apache.lucene.document.Document;

import junit.framework.TestCase;
import net.ion.framework.util.DateUtil;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;

public class TestLoadDoc  extends TestCase {

	public void testIndex() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).stext("text", "hello world").date("birth", new Date()).insert() ;
				return null;
			}
		}) ;

		assertEquals(1, cen.newSearcher().createRequest("name:bleujin").find().totalCount()) ;
		assertEquals(1, cen.newSearcher().createRequest("text:hello").find().totalCount()) ;
		assertEquals(1, cen.newSearcher().createRequest("age:20").find().totalCount()) ;
		assertEquals(1, cen.newSearcher().createRequest("birth:" + DateUtil.currentDateToString("yyyyMMdd")).find().totalCount()) ;
		
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.loadDocument("bleujin", true, new FieldLoadable(){
					@Override
					public WriteDocument handle(WriteDocument result, Document findDoc) throws IOException {
						result.number("age", findDoc.getField("age").numericValue().longValue()).keyword("name", "hero").stext("text", findDoc.getField("text").stringValue()).updateVoid() ;
						return null;
					}
				}) ;
				return null;
			}
		}) ;

		assertEquals(1, cen.newSearcher().createRequest("name:hero").find().totalCount()) ;
		assertEquals(1, cen.newSearcher().createRequest("text:hello").find().totalCount()) ;
		assertEquals(1, cen.newSearcher().createRequest("age:20").find().totalCount()) ;
	}
}
