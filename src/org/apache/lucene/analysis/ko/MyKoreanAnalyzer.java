package org.apache.lucene.analysis.ko;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;

import net.ion.framework.util.IOUtil;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public class MyKoreanAnalyzer extends StopwordAnalyzerBase {

	/** Default maximum allowed token length */
	public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

	private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

	private boolean bigrammable = false;

	private boolean hasOrigin = false;

	private boolean exactMatch = false;
	private boolean originCNoun = true;
	private boolean queryMode = false;
	private boolean wordSegment = false;

	/** An unmodifiable set containing some common words that are usually not useful for searching. */
	public static final CharArraySet STOP_WORDS_SET;
	static {
		try {
			STOP_WORDS_SET = loadStopwordSet(false, MyKoreanAnalyzer.class, "stopwords.txt", "#");
		} catch (IOException ioe) {
			throw new Error("Cannot load stop words", ioe);
		}
	}

	public MyKoreanAnalyzer() {
		this(Version.LUCENE_46, STOP_WORDS_SET);
	}

	/**
	 * 검색을 위한 형태소분석
	 * 
	 * @param search
	 */
	public MyKoreanAnalyzer(boolean exactMatch) {
		this(Version.LUCENE_46, STOP_WORDS_SET);
		this.exactMatch = exactMatch;
	}
	
	public MyKoreanAnalyzer(Version matchVersion, String[] stopWords) throws IOException {
		this(matchVersion, StopFilter.makeStopSet(matchVersion, stopWords));
	}

	public MyKoreanAnalyzer(Version matchVersion) throws IOException {
		this(matchVersion, STOP_WORDS_SET);
	}

	public MyKoreanAnalyzer(Version matchVersion, File stopwords) throws IOException {
		this(matchVersion, loadStopwordSet(stopwords, matchVersion));
	}

	public MyKoreanAnalyzer(Version matchVersion, File stopwords, String encoding) throws IOException {
		this(matchVersion, loadStopwordSet(stopwords, matchVersion));
	}

	public MyKoreanAnalyzer(Version matchVersion, Reader stopwords) throws IOException {
		this(matchVersion, loadStopwordSet(stopwords, matchVersion));
	}

	public MyKoreanAnalyzer(Version matchVersion, CharArraySet stopWords) {
		super(matchVersion, stopWords);
	}

	public Version getVersion(){
		return super.getVersion() ;
	}
	
	@Override
	protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
		final KoreanTokenizer src = new KoreanTokenizer(reader);
		// src.setMaxTokenLength(maxTokenLength);
		TokenStream tok = new LowerCaseFilter(getVersion(), src);
		tok = new ClassicFilter(tok);
		tok = new KoreanFilter(tok, bigrammable, hasOrigin, exactMatch, originCNoun, queryMode);
		if (wordSegment)
			tok = new WordSegmentFilter(tok, hasOrigin);
		tok = new HanjaMappingFilter(tok);
		tok = new PunctuationDelimitFilter(tok);
		tok = new StopFilter(getVersion(), tok, stopwords);
		return new TokenStreamComponents(src, tok) {
			@Override
			protected void setReader(final Reader reader) throws IOException {
				// src.setMaxTokenLength(KoreanAnalyzer.this.maxTokenLength);
				super.setReader(reader);
			}
		};
	}

	/**
	 * determine whether the bigram index term is returned or not if a input word is failed to analysis If true is set, the bigram index term is returned. If false is set, the bigram index term is not returned.
	 */
	public void setBigrammable(boolean is) {
		bigrammable = is;
	}

	/**
	 * determin whether the original term is returned or not if a input word is analyzed morphically.
	 */
	public void setHasOrigin(boolean has) {
		hasOrigin = has;
	}

	/**
	 * determin whether the original compound noun is returned or not if a input word is analyzed morphically.
	 */
	public void setOriginCNoun(boolean cnoun) {
		originCNoun = cnoun;
	}

	/**
	 * determin whether the original compound noun is returned or not if a input word is analyzed morphically.
	 */
	public void setExactMatch(boolean exact) {
		exactMatch = exact;
	}

	/**
	 * determin whether the analyzer is running for a query processing
	 */
	public void setQueryMode(boolean mode) {
		queryMode = mode;
	}

	/**
	 * determin whether word segment analyzer is processing
	 */
	public boolean isWordSegment() {
		return wordSegment;
	}

	public void setWordSegment(boolean wordSegment) {
		this.wordSegment = wordSegment;
	}

	
	public static class WordGroup {
		private WordGroup() {
		};

		public static String testAdjustReader(String str) throws IOException {
			return IOUtil.toString(WordGroup.adjustReader(new StringReader(str)));
		}

		static Reader adjustReader(Reader reader) {
			// return reader ;
			try {
				StringBuilder result = new StringBuilder(40);
				int pretype = 0;
				// char[] cbuffr = new char[100];
				CharBuffer cbuffer = CharBuffer.allocate(40);
				while (reader.read(cbuffer) != -1) {
					for (char c : cbuffer.array()) {
						int currtype = Character.getType(c);
						if (isSplit(pretype, currtype))
							result.append(' ');
						result.append(c);
						pretype = currtype;
					}
					cbuffer.flip();
				}
				return new StringReader(result.toString());
			} catch (IOException ex) {
				ex.printStackTrace();
				return reader;
			}
		}

		private static boolean isSplit(int pre, int curr) {
			if (pre == 5 && curr == 1)
				return true;
			if (pre == 5 && curr == 2)
				return true;
			if (pre == 1 && curr == 5)
				return true;
			if (pre == 2 && curr == 5)
				return true;
			if (pre == 9 && curr == 5)
				return true;
			if (curr == 20)
				return true;
			return false;
		}
	}
}

