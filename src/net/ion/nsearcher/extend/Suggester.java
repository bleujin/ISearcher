package net.ion.nsearcher.extend;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.config.Central;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;

public class Suggester {

	private Central central;
	private AnalyzingSuggester inner;
	
	public Suggester(Central central, Analyzer analyzer) {
		this.central = central ;
		this.inner = new AnalyzingSuggester(analyzer);
	}
	
	
	public Suggester build(String fieldName) throws IOException {
		Dictionary dict = new LuceneDictionary(central.newSearcher().indexReader(), fieldName); // new WordFreqArrayIterator(wordFreqs)
		inner.build(dict);

		return this ;
	}


	public List<KeyValue> lookup(String key, int num) throws IOException {
		List<KeyValue> result = ListUtil.newList() ;
		List<LookupResult> founds = inner.lookup(key, false, num);
		
		for(LookupResult found : founds){
			result.add(new DefaultKeyValue(found.key, found.value));
		}
		
		return result ;
	}

}
