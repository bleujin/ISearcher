package net.ion.bleujin.lucene.indexwriter;


import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.instantiated.InstantiatedIndex;
import org.apache.lucene.store.instantiated.InstantiatedIndexReader;

public class DicTest extends ISTestCase{

	public void testDic() throws Exception {
		Central cen = writeDocument() ;
		
		
		Indexer indexer = cen.newIndexer() ;
		indexer.index(createKoreanAnalyzer(), new IndexJob<Void>() {

			public Void handle(IndexSession session) throws Exception {
				session.deleteAll() ;
				MyDocument doc = MyDocument.testDocument() ;
				doc.add(MyField.text("bleujin", "태극기가 바람에 펄럭입니다. 오영준 삼성신한생명LGU+보증보험")) ;
				session.updateDocument(doc) ;
				return null;
			}
		}) ;
		
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
