package org.apache.lucene.analysis.ko;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.cjk.CJKTokenizer;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

/**
 * An {@link Analyzer} that tokenizes text with {@link StandardTokenizer}, normalizes content with {@link CJKWidthFilter}, folds case with {@link LowerCaseFilter}, forms bigrams of CJK with {@link CJKBigramFilter}, and filters stopwords with {@link StopFilter}
 */
public final class DCJKAnalyzer extends StopwordAnalyzerBase {
	/**
	 * File containing default CJK stopwords.
	 * <p/>
	 * Currently it contains some common English words that are not usually useful for searching and some double-byte interpunctions.
	 */
	public final static String DEFAULT_STOPWORD_FILE = "stopwords.txt";

	/**
	 * Returns an unmodifiable instance of the default stop-words set.
	 * 
	 * @return an unmodifiable instance of the default stop-words set.
	 */
	public static CharArraySet getDefaultStopSet() {
		return DefaultSetHolder.DEFAULT_STOP_SET;
	}

	private static class DefaultSetHolder {
		static final CharArraySet DEFAULT_STOP_SET;

		static {
			try {
				DEFAULT_STOP_SET = loadStopwordSet(false, CJKAnalyzer.class, DEFAULT_STOPWORD_FILE, "#");
			} catch (IOException ex) {
				// default set should always be present as it is part of the
				// distribution (JAR)
				throw new RuntimeException("Unable to load default stopword set");
			}
		}
	}

	/**
	 * Builds an analyzer which removes words in {@link #getDefaultStopSet()}.
	 */
	public DCJKAnalyzer(Version matchVersion) {
		this(matchVersion, DefaultSetHolder.DEFAULT_STOP_SET);
	}

	/**
	 * Builds an analyzer with the given stop words
	 * 
	 * @param matchVersion
	 *            lucene compatibility version
	 * @param stopwords
	 *            a stopword set
	 */
	public DCJKAnalyzer(Version matchVersion, CharArraySet stopwords) {
		super(matchVersion, stopwords);
	}

	public Version getVersion(){
		return super.getVersion() ;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		
//		String readerString = "" ;
//		try {
//			readerString = IOUtil.toStringWithClose(reader);
//			Debug.line(readerString) ;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		StringReader copy = new StringReader(readerString) ;
		
		if (getVersion().onOrAfter(Version.LUCENE_36)) {
			final Tokenizer source = new StandardTokenizer(getVersion(), reader);
			// run the widthfilter first before bigramming, it sometimes combines characters.
			TokenStream result = new CJKWidthFilter(source);
			result = new LowerCaseFilter(getVersion(), result);
			result = new CJKBigramFilter(result);
			return new TokenStreamComponents(source, new StopFilter(getVersion(), result, stopwords));
		} else {
			final Tokenizer source = new CJKTokenizer(reader);
			return new TokenStreamComponents(source, new StopFilter(getVersion(), source, stopwords));
		}
	}
}
