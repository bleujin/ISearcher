package net.ion.nsearcher.index;

import java.io.IOException;

import junit.framework.TestCase;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;

import org.apache.lucene.analysis.Analyzer;

public class TestIndexSession extends TestCase{

	public void testAppendFrom() throws Exception {
		final Central cs = CentralConfig.newRam().build() ;
		Central ct = CentralConfig.newRam().build() ;
		
		Analyzer anal = new MyKoreanAnalyzer() ;

		cs.newIndexer().asyncIndex("hero", anal, new AddFiveEntryJob("hero")).get();
		ct.newIndexer().asyncIndex("bleujin", anal, new AddFiveEntryJob("bleujin")).get() ;
		
		ct.newIndexer().index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.appendFrom(cs.dir()) ;
				return null;
			}
		}) ;
		assertEquals(10, ct.newSearcher().search("").totalCount()) ;
	}
	
}


class AddFiveEntryJob implements IndexJob<Boolean> {
	String name ;
	AddFiveEntryJob(String name){
		this.name = name ;
	}
	
	public Boolean handle(IndexSession session) throws IOException {
		for (int i : ListUtil.rangeNum(5)) {
			session.insertDocument(MyDocument.testDocument().add(MyField.number("index", i)).add(MyField.keyword("name", name))) ;
		}
		return true;
	}
}