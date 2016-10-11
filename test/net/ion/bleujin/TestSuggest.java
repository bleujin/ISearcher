package net.ion.bleujin;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

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
				isession.newDocument().text("subject", "crawl").insert();
				isession.newDocument().text("subject", "한글날").insert();
				isession.newDocument().text("subject", "한글 사랑").insert();
				return null;
			}
		});

	}

	public void testSugg() throws Exception {
		AnalyzingSuggester suggester = new AnalyzingSuggester(central.indexConfig().indexAnalyzer());
		Dictionary dict = new LuceneDictionary(central.newSearcher().indexReader(), "subject"); // new WordFreqArrayIterator(wordFreqs)
		suggester.build(dict);

		List<LookupResult> results = suggester.lookup("한글", false, 100);

		System.out.println("Suggested words for input \"ba\"");
		for (LookupResult lookupResult : results) {
			System.out.println(lookupResult.key + ":" + lookupResult.value);
		}
	}
	
	public void testFileDictionary() throws Exception {
		
		AnalyzingSuggester suggester = new AnalyzingSuggester(central.indexConfig().indexAnalyzer());
		
		Reader reader = new StringReader("word1\t100\r\nword2 word3\t101\r\nword2 word5\t102") ;
		Dictionary dict = new FileDictionary(reader, "\t") ;
		suggester.build(dict);
		
		List<LookupResult> results = suggester.lookup("word2", false, 100);

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
