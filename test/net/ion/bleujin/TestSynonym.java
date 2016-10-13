package net.ion.bleujin;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.Version;

public class TestSynonym extends TestCase {

	private Central central;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.central = CentralConfig.newRam().indexConfigBuilder().parent().searchConfigBuilder().queryAnalyzer(createAnal()).build();
		central.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().text("name", "bleujin").insert();
				isession.newDocument().text("name", "bleuhero").insert();
				isession.newDocument().text("name", "bleujini").insert();
				isession.newDocument().text("name", "jini").insert();
				isession.newDocument().text("name", "jin").insert();
				return null;
			}
		});
	}

	public void testSynonym() throws Exception {
		central.newSearcher().createRequest("bleujin").find().debugPrint();
		Debug.line();
		central.newSearcher().createRequest("jini").find().debugPrint();

		
	}

	public Analyzer createAnal() throws Exception {
		return new Analyzer() {
			@Override
			public TokenStreamComponents createComponents(String field, Reader reader) {

				Tokenizer tokenizer = new StandardTokenizer(SearchConstant.LuceneVersion, reader);
				TokenStream ts = new LowerCaseFilter(SearchConstant.LuceneVersion, tokenizer);
				ts = new PorterStemFilter(ts);

				CharArraySet stopwords = new CharArraySet(10, true);
				stopwords.add("a");
				stopwords.add("in");
				ts = new StopFilter(SearchConstant.LuceneVersion, ts, stopwords);

				SynonymMap smap = null;
				try {
					SynonymMap.Builder sb = new SynonymMap.Builder(true);
					sb.add(new CharsRef("bleujin"), new CharsRef("bleuhero"), true);
					sb.add(new CharsRef("bleujin"), new CharsRef("bleujini"), true);

					sb.add(new CharsRef("jini"), new CharsRef("jin"), true);

					smap = sb.build();

				} catch (IOException ex) {
					ex.printStackTrace(System.err);
				}

				ts = new SynonymFilter(ts, smap, true);
				return new TokenStreamComponents(tokenizer, ts);
			}
		};
	}

}
