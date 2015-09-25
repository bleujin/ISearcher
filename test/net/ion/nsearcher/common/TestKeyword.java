package net.ion.nsearcher.common;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.index.Term;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.search.filter.TermFilter;

public class TestKeyword extends TestCase {

	public void testKeyword() throws Exception {
		Central cen = CentralConfig.newRam().build();
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument("test").keyword("path", "/bleujin/hero");
				isession.insertDocument(doc) ;
				return null;
			}
		});
		
		assertEquals(1, cen.newSearcher().createRequest("").setFilter(new TermFilter("path", "/bleujin/hero")).find().size()) ;
		cen.destroySelf() ;
	}
	
	
	public void testDualKeyword() throws Exception {
		Central cen = CentralConfig.newRam().indexConfigBuilder().indexAnalyzer(new CJKAnalyzer()).parent().searchConfigBuilder().queryAnalyzer(new CJKAnalyzer()).build() ;
		cen.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
//				isession.newDocument("hi").keyword("myname", "before.png").keyword("myname", "a1.jpeg").insert() ;
				isession.newDocument("hello").keyword("myname", "005.jpeg").keyword("myname", "").insert() ;
				return null;
			}
		}) ;
		
		cen.newSearcher().createRequest("myname:005.jpeg").find().debugPrint();
		
		Debug.line(cen.newSearcher().createRequest("myname:\"005.jpeg\"").query(), cen.newSearcher().createRequest(new Term("myname", "005.jpeg")).query()) ;
		
//		cen.newSearcher().createRequest(new Term("myname", "005.jpeg")).find().debugPrint();
		
//		cen.newSearcher().createRequest("myname:before.png").find().debugPrint();
//		cen.newSearcher().createRequest("myname:a1.jpeg").find().debugPrint();
	}

	
	public void testSubString() throws Exception {
		Debug.line(StringUtil.substringBefore(null, "."));
	}
}
