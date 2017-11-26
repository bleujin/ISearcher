package net.ion.nsearcher.extend;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

import org.apache.commons.collections.KeyValue;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.TermFreqIterator;
import org.apache.lucene.search.suggest.FileDictionary;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;
import org.apache.lucene.util.BytesRef;

public class TestSuggest extends TestCase {

	private Central central;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.central = CentralConfig.newRam().build();
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().text("subject", "한글").insert();
				isession.newDocument().text("subject", "한글").insert();
				isession.newDocument().text("subject", "한글").insert();
				isession.newDocument().text("subject", "한글").insert();
				isession.newDocument().text("subject", "crawl").insert();
				isession.newDocument().text("subject", "한글날 한글을").insert();
				isession.newDocument().text("subject", "한한글 사랑").insert();
				return null;
			}
		});
	}
	
	public void testInterface() throws Exception {
		Suggester s = central.newSuggester() ;
		
		List<KeyValue> found = s.lookup("한", 3) ;
		for (KeyValue entry : found) {
			Debug.line(entry.getKey(), entry.getValue()); 
		}
		s.build("subject") ;

		found = central.newSuggester().lookup("사랑", 2) ;
		for (KeyValue entry : found) {
			Debug.line(entry.getKey(), entry.getValue()); 
		}
	}
	
	public void testSugg() throws Exception {
		AnalyzingSuggester suggester = new AnalyzingSuggester(central.indexConfig().indexAnalyzer());
		Dictionary dict = new LuceneDictionary(central.newSearcher().indexReader(), "subject"); // new WordFreqArrayIterator(wordFreqs)
		suggester.build(dict);

		List<LookupResult> results = suggester.lookup("한", false, 100);

		for (LookupResult lookupResult : results) {
			System.out.println(lookupResult.key + ":" + lookupResult.value);
		}
	}
	
	public void testFileDictionary() throws Exception {
		
		AnalyzingSuggester suggester = new AnalyzingSuggester(central.indexConfig().indexAnalyzer());
		
		Reader reader = new StringReader("word1\t100\r\nword2 word3\t101\r\nword2 word5\t102") ;
		Dictionary dict = new FileDictionary(reader, "\t") ;
		suggester.build(dict);
		
		List<LookupResult> results = suggester.lookup("word3", false, 100);

		for (LookupResult lookupResult : results) {
			System.out.println(lookupResult.key + ":" + lookupResult.value);
		}
	}
	

	public void testTerms() throws Exception {
		IndexReader reader = central.newSearcher().indexReader();
		AtomicReader aReader = SlowCompositeReaderWrapper.wrap(reader); // Should use reader.leaves instead ?
		Terms terms = aReader.terms("subject");
		TermsEnum termEnum = terms.iterator(null);
		TermFreqIterator wrapper = new TermFreqIterator.TermFreqIteratorWrapper(termEnum);

		BytesRef br = null ;
		while( (br = wrapper.next()) != null){
			Debug.line(br.utf8ToString());
		}
	}
}
