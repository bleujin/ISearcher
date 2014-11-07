package net.ion.nsearcher.index;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;

public class TestIndexSession extends TestCase{

	public void testAppendFrom() throws Exception {
		final Central cs = CentralConfig.newRam().build() ;
		Central ct = CentralConfig.newRam().build() ;
		
		cs.newIndexer().asyncIndex("hero", new AddFiveEntryJob("hero")).get();
		ct.newIndexer().asyncIndex("bleujin", new AddFiveEntryJob("bleujin")).get() ;
		
		ct.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.appendFrom(cs.dir()) ;
				return null;
			}
		}) ;
		
		assertEquals(10, ct.newSearcher().search("").size()) ;
	}
	
	public void testCommitUserData() throws Exception {
		final Central cs = CentralConfig.newRam().build() ;

		Analyzer anal = new MyKoreanAnalyzer() ;

		cs.newIndexer().index("hero", anal, new AddFiveEntryJob("hero"));
		cs.newIndexer().index("bleujin", anal, new AddFiveEntryJob("bleujin"));
		Map<String, String> map = cs.newReader().commitUserData();
		assertEquals(true, map.containsKey("lastmodified"));
		assertEquals(true, map.containsKey("version"));
	}
	
	
	public void testBeforeCommit() throws Exception {
		final Central cs = CentralConfig.newRam().build() ;
		
		cs.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).update() ;
				
				Debug.line(isession.newDocument("bleujin").fields()) ;
				return null;
			}
		}) ;
		cs.close();
	}
	
	
	
}


class AddFiveEntryJob implements IndexJob<Boolean> {
	String name ;
	AddFiveEntryJob(String name){
		this.name = name ;
	}
	
	public Boolean handle(IndexSession isession) throws IOException {
		for (int i : ListUtil.rangeNum(5)) {
			isession.insertDocument(isession.newDocument().add(MyField.number("index", i)).add(MyField.keyword("name", name))) ;
		}
		return true;
	}
}