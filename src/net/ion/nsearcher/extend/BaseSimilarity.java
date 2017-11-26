package net.ion.nsearcher.extend;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.surround.query.SrndPrefixQuery;
import org.apache.lucene.util.BytesRef;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.nsearcher.search.SimilaryDocs;

public class BaseSimilarity {

	private SearchResponse searchResponse;
	private ReadDocument target;
	public BaseSimilarity(SearchResponse searchResponse, ReadDocument target) {
		this.searchResponse = searchResponse ;
		this.target = target ;
	}

	public SimilaryDocs foundBy(String field) throws IOException {
		
		IndexReader ireader = searchResponse.request().searcher().indexReader() ;
		Set<String> allTerms = new HashSet();
		
		Map<String, Integer> targetMap = termFrequencies(ireader, allTerms, target.docId(), field) ;
		Map<Integer, Map<String, Integer>> temp = MapUtil.newMap() ;
		for(int docId : searchResponse.docIds()) {
			if (docId == target.docId()) continue ;
			Map<String, Integer> tf = termFrequencies(ireader, allTerms, docId, field) ;
			temp.put(docId, tf) ;
		}
		
		RealVector targetVector = realVector(allTerms, targetMap) ;
		List<SimilaryDoc> result = ListUtil.newList() ;
		for (int docId : temp.keySet()) {
			Map<String, Integer> tf = temp.get(docId) ;
			RealVector rv = realVector(allTerms, tf) ;
			double simValue = (targetVector.dotProduct(rv)) / (targetVector.getNorm() * rv.getNorm()) ;
			result.add(new SimilaryDoc(docId, simValue));
		}
		
		Collections.sort(result);
		Collections.reverse(result);
		return new SimilaryDocs(searchResponse, target, result);
	}
	
	
	private Map<String, Integer> termFrequencies(IndexReader reader, Set<String> allTerms, int docId, String field) throws IOException {
		Terms vector = reader.getTermVector(docId, field);
		if (vector == null) throw new IllegalStateException("field not exist or, not indexed with vector") ;
		TermsEnum termsEnum = null;
		termsEnum = vector.iterator(termsEnum);
		Map<String, Integer> frequencies = new HashMap();
		BytesRef text = null;
		while ((text = termsEnum.next()) != null) {
			String term = text.utf8ToString();
			int freq = (int) termsEnum.totalTermFreq();
			frequencies.put(term, freq);
			allTerms.add(term);
		}
		return frequencies;
	}
	
	private RealVector realVector(Set<String> allTerms, Map<String, Integer> map) {
		RealVector vector = new ArrayRealVector(allTerms.size());
		int i = 0;
		for (String term : allTerms) {
			int value = map.containsKey(term) ? map.get(term) : 0;
			vector.setEntry(i++, value);
		}
		return (RealVector) vector.mapDivide(vector.getL1Norm());
	}


}



