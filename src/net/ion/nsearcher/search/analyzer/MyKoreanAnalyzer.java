package net.ion.nsearcher.search.analyzer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.ion.framework.util.IOUtil;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.kr.KoreanFilter;
import org.apache.lucene.analysis.kr.KoreanTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public class MyKoreanAnalyzer extends StopwordAnalyzerBase {

	public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

	private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

	private final boolean replaceInvalidAcronym;

	private Set stopSet;

	private boolean bigrammable = true;

	private boolean hasOrigin = true;

	private boolean exactMatch = false;

	private boolean originCNoun = true;

	public static final String DIC_ENCODING = "UTF-8";

	public static final CharArraySet STOP_WORDS_SET;

	static {
		List stopWords = Arrays.asList(new String[] { "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with", "이", "그", "저",
				"것", "수", "등", "들", "및", "에서", "그리고", "그래서", "또", "또는" });

		CharArraySet stopSet = new CharArraySet(Version.LUCENE_42, stopWords.size(), false);

		stopSet.addAll(stopWords);
		STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
	}

	public MyKoreanAnalyzer() {
		this(Version.LUCENE_42, STOP_WORDS_SET);
	}

	/**
	 * 검색을 위한 형태소분석
	 * 
	 * @param search
	 */
	public MyKoreanAnalyzer(boolean exactMatch) {
		this(Version.LUCENE_42, STOP_WORDS_SET);
		this.exactMatch = exactMatch;
	}

	public MyKoreanAnalyzer(Version matchVersion, String[] stopWords) {
		this(matchVersion, StopFilter.makeStopSet(matchVersion, stopWords));
	}

	public MyKoreanAnalyzer(Version matchVersion)  {
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
		replaceInvalidAcronym = matchVersion.onOrAfter(Version.LUCENE_42);
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {

		final KoreanTokenizer src = new KoreanTokenizer(matchVersion, WordGroup.adjustReader(reader));
		src.setMaxTokenLength(maxTokenLength);

		TokenStream tok = new KoreanFilter(src, bigrammable, hasOrigin, exactMatch, originCNoun);
		tok = new StopFilter(matchVersion, new LowerCaseFilter(matchVersion, tok), stopwords);
		return new TokenStreamComponents(src, tok) {
			@Override
			protected void setReader(final Reader reader) throws IOException {
				src.setMaxTokenLength(MyKoreanAnalyzer.this.maxTokenLength);
				super.setReader(WordGroup.adjustReader(reader));
			}
		};
	}
	
//	protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
//		final StandardTokenizer src = new StandardTokenizer(matchVersion, reader);
//		src.setMaxTokenLength(maxTokenLength);
//		TokenStream tok = new StandardFilter(matchVersion, src);
//		tok = new LowerCaseFilter(matchVersion, tok);
//		tok = new StopFilter(matchVersion, tok, stopwords);
//		return new TokenStreamComponents(src, tok) {
//			@Override
//			protected void setReader(final Reader reader) throws IOException {
//				src.setMaxTokenLength(StandardAnalyzer.this.maxTokenLength);
//				super.setReader(reader);
//			}
//		};
//	}
	

	public void setBigrammable(boolean is) {
		bigrammable = is;
	}

	public void setHasOrigin(boolean has) {
		hasOrigin = has;
	}

	public void setOriginCNoun(boolean cnoun) {
		originCNoun = cnoun;
	}

	public void setExactMatch(boolean exact) {
		exactMatch = exact;
	}

}



class WordGroup {
	private WordGroup() {
	};

	public static String testAdjustReader(String str) throws IOException {
		return IOUtil.toString(WordGroup.adjustReader(new StringReader(str)));
	}

	static Reader adjustReader(Reader reader) {
//		return reader ;
		try {
			StringBuilder result = new StringBuilder(40);
			int pretype = 0;
//			char[] cbuffr = new char[100];
			CharBuffer cbuffer = CharBuffer.allocate(40);
			while (reader.read(cbuffer) != -1) {
				for (char c : cbuffer.array()) {
					int currtype = Character.getType(c);
					if (isSplit(pretype, currtype))
						result.append(' ');
					result.append(c);
					pretype = currtype;
				}
				cbuffer.flip() ;
			}
			return new StringReader(result.toString());
		} catch (IOException ex) {
			ex.printStackTrace() ;
			return new StringReader("");
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
