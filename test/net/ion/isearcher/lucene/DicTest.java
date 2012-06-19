package net.ion.isearcher.lucene;


import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.PrefixTermEnum;
import org.apache.lucene.search.WildcardTermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.instantiated.InstantiatedIndex;
import org.apache.lucene.store.instantiated.InstantiatedIndexReader;
import org.apache.lucene.util.Version;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.indexer.write.IWriter;

public class DicTest extends ISTestCase{

	public void testDic() throws Exception {
		Directory dir = writeDocument() ;
		Central cen = Central.createOrGet(dir) ;
		
		IWriter indexer = cen.newIndexer(new CJKAnalyzer(Version.LUCENE_CURRENT)) ;
		indexer.begin("my_test") ;
		indexer.deleteAll() ;
		MyDocument doc = MyDocument.testDocument() ;
		doc.add(MyField.text("bleujin", "태극기가 바람에 펄럭입니다. 오영준 삼성신한생명LGU+보증보험")) ;
		indexer.updateDocument(doc) ;
		
		indexer.end() ;
		
		// name, date, subject
		String field = "name" ;
		IndexReader reader = cen.newReader().getIndexReader() ;
		
		InstantiatedIndex iidx = new InstantiatedIndex(reader) ;
		TermEnum tenum = new InstantiatedIndexReader(iidx).terms() ;
		
		Debug.debug(reader.maxDoc()) ;
		
		while(tenum.next()){
			Term term = tenum.term();
			Debug.debug(term, term.field(), term.text()) ;
		}
	}
}
