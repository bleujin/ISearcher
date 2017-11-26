package net.ion.nsearcher.extend;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.extend.BaseSimilarity;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.EachDocHandler;
import net.ion.nsearcher.search.EachDocIterator;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.SimilaryDocs;

public class TestSimilarity extends TestCase {

	
	public void testFindSimilaryDoc() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		cen.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("111").vtext("cook", "육류, 치즈").vtext("note", "깊고 짙은 레드 컬러로 믿을 수 없을 정도로 진한 체리, 블랙 커런트, 흙 자두의 풍미가 토스트된 오크향과 함께 조화롭게 어우러져 피어오른다. 농익은 과일류의 풍미와 탄닌 균형감이 인상적이다.").insertVoid();
				isession.newDocument("222").vtext("cook", "육류, 치즈").vtext("note", "블랙 커런트와 절인 체리와 같은 아주 짙은 아로마를 느낄 수 있다. 잼과 같은 리치한 맛과 신선한 과실의 피니시를 동시에 느낄 수 있다. ").insertVoid();
				isession.newDocument("333").vtext("cook", "샐러드, 해산물, 치즈").vtext("note", "눈부신 페일 옐로우 계열, 아몬드 계열의 컬러감을 느낄 수 있다. 파인애플, 매우 신선한 레몬과 자몽향에 화이트 플로랄 계열로 소비뇽 블랑 특유의 향를 잘 나타낸다. 신선하고 아로마틱한 향과 더불어 크리스피한 느낌을 느낄 수 있다. 가벼운 바디감으로 음식과의 매칭이나 가볍게 즐기기에 제격이다.").insertVoid();
				return null;
			}
		}) ;
		
		SearchResponse targetGroup = cen.newSearcher().createRequest("").find() ;
		BaseSimilarity bs = targetGroup.similarity(targetGroup.first()) ;
		
		SimilaryDocs sd = bs.foundBy("note") ;
		sd.limit(5).overWeight(0.01d).debugPrint();
		
	}
	
}