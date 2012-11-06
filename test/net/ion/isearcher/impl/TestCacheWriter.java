package net.ion.isearcher.impl;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

public class TestCacheWriter extends TestCase{

	public void testCreate() throws Exception {
		Central cen = Central.createOrGet(new RAMDirectory()) ;
		
		CacheWriter cw = CacheWriter.create(cen, new MyKoreanAnalyzer()) ;
		cw.begin("cache") ;
		
		for (int i : ListUtil.rangeNum(10)) {
			MyDocument doc = MyDocument.testDocument().add(MyField.number("index", i)).add(MyField.keyword("name", "bleujin")) ;
			cw.insertDocument(doc) ;
		}
		
		cw.end() ;
		
		ISearcher searcher = cen.newSearcher() ;
		assertEquals(10, searcher.searchTest("").getTotalCount()) ;
	}
	
	public void testReWrite() throws Exception {
		Central cen = Central.createOrGet(new RAMDirectory()) ;
		
		CacheWriter cw = CacheWriter.create(cen, new MyKoreanAnalyzer()) ;
		cw.begin("cache") ;
		for (int i : ListUtil.rangeNum(5)) {
			MyDocument doc = MyDocument.testDocument().add(MyField.number("index", i)).add(MyField.keyword("name", "bleujin")) ;
			cw.insertDocument(doc) ;
		}
		cw.end() ;
		
		ISearcher searcher = cen.newSearcher() ;
		assertEquals(5, searcher.searchTest("").getTotalCount()) ;

		// rewrite.
		cw = CacheWriter.create(cen, new MyKoreanAnalyzer()) ;
		cw.begin("cache") ;
		
		for (int i : ListUtil.rangeNum(5, 10)) {
			MyDocument doc = MyDocument.testDocument().add(MyField.number("index", i)).add(MyField.keyword("name", "bleujin")) ;
			cw.insertDocument(doc) ;
		}
		cw.end() ;
		
		searcher = cen.newSearcher() ;
		assertEquals(10, searcher.searchTest("").getTotalCount()) ;
	}
	
	public void testUpdate() throws Exception {
		RAMDirectory ramDir = new RAMDirectory();
		Central cen = Central.createOrGet(ramDir) ;
		IWriter writer = cen.newIndexer(new MyKoreanAnalyzer()) ;
		writer.begin("owner") ;
		MyDocument doc = MyDocument.testDocument().add(MyField.number("index", 0)).add(MyField.keyword("name", "bleujin")) ;
		writer.insertDocument(doc) ;
		writer.end() ;
		

		CacheWriter cw = CacheWriter.create(cen, new MyKoreanAnalyzer()) ;
		cw.begin("cache") ;
		doc.add(MyField.number("index", 0)).add(MyField.keyword("name", "hero")) ;
		cw.updateDocument(doc) ;
		cw.end() ;

		ISearcher searcher = cen.newSearcher() ;
		assertEquals(1, searcher.searchTest("").getTotalCount()) ;
		
	}
	

	public void testDuplicated() throws Exception {
		RAMDirectory ramDir = new RAMDirectory();
		Central cen = Central.createOrGet(ramDir) ;
		IWriter writer = cen.newIndexer(new MyKoreanAnalyzer()) ;
		writer.begin("owner") ;
		MyDocument doc = MyDocument.testDocument().add(MyField.number("index", 0)).add(MyField.keyword("name", "bleujin")) ;
		writer.insertDocument(doc) ;
		writer.end() ;
		

		CacheWriter cw = CacheWriter.create(cen, new MyKoreanAnalyzer()) ;
		cw.begin("cache") ;
		doc.add(MyField.number("index", 0)).add(MyField.keyword("name", "hero")) ;
		cw.updateDocument(doc) ;
		cw.end() ;

		ISearcher searcher = cen.newSearcher() ;
		assertEquals(1, searcher.searchTest("").getTotalCount()) ;
		
	}	
	
	
	
	
}
