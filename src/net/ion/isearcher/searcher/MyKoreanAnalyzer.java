package net.ion.isearcher.searcher;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Stack;

import net.ion.framework.convert.html.Working;
import net.ion.framework.rope.RopeReader;
import net.ion.framework.rope.RopeWriter;
import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringBufferReader;
import net.ion.framework.util.StringBuilderReader;
import net.ion.framework.util.StringUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.util.Version;

public class MyKoreanAnalyzer extends Analyzer {

	private KoreanAnalyzer kor = new KoreanAnalyzer();
	private String[] stopword;

	public MyKoreanAnalyzer() {
		this.kor = new KoreanAnalyzer();
		this.stopword = new String[0];
	}

	public MyKoreanAnalyzer(String[] stopword){
		try {
			this.kor = new KoreanAnalyzer(Version.LUCENE_CURRENT, stopword);
		} catch (IOException e) {
			this.kor = new KoreanAnalyzer() ;
		}
		this.stopword = stopword;
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return kor.tokenStream(fieldName, WordGroup.adjustReader(stopword, reader));
	}

	public String[] getStopword(){
		return stopword ;
	}
	
}

class WordGroup {
	private List<Word> words = ListUtil.newList();

	private WordGroup() {
	};

	void add(int type, StringBuilder word) {
		// if (Word.isIgnoreType(type)) return ;
		words.add(Word.create(type, word));
	}

	public static String testAdjustReader(String str) throws IOException{
		return IOUtil.toString(WordGroup.adjustReader(new String[0], new StringReader(str))) ;
	} 
	
	public static Reader adjustReader(String[] stopWord, Reader reader) {
		try {
			String strValue = IOUtil.toString(reader);

			int preType = Character.getType(strValue.charAt(0));

			WordGroup words = WordGroup.create();
			StringBuilder word = new StringBuilder();
			for (Character c : strValue.toCharArray()) {
				if (Character.getType(c) == preType) {
					word.append(c);
					continue;
				} else {
					words.add(preType, word);

					word = new StringBuilder("" + c);
					preType = Character.getType(c);
				}
			}
			words.add(preType, word);

			StringBuilder result = words.toAdjustBuilder(stopWord);
			return new StringBuilderReader(result);
		} catch (IOException e) {
			return reader;
		}
	}

	static WordGroup create() {
		return new WordGroup();
	}

	private Word[] toAdjustArray() {
		Stack<Word> result = new Stack<Word>();

		for (Word word : words) {
			if (result.empty())
				result.add(word);
			else {
				Word bword = result.pop();
				if (bword.type() == 1 && word.type() == 5)
					result.add(Word.create(255, bword.value().append(word.value()))); // 1:5 E플러스
				else if (bword.type() == 9 && word.type() == 5)
					result.add(Word.create(255, bword.value().append(word.value()))); // 9:5 5월
				else if (bword.type() == 1 && word.type() == 9)
					result.add(Word.create(255, bword.value().append(word.value()))); // 1:9 B500
				else if (bword.type() == 24 && word.type() == 9)
					result.add(Word.create(255, bword.value().append(word.value())));
				else {
					result.add(bword);
					result.add(word);
				}
			}
		}
		return result.toArray(new Word[0]);
	}

	StringBuilder toAdjustBuilder(String[] stopWord) {
		StringBuilder result = new StringBuilder();
		for (Word word : toAdjustArray()) {
			if (word.isIgnoreType())
				continue; // result.append('') ;
			else if (word.value().length() <= 1){
				continue;
			} else if (ArrayUtil.contains(stopWord, word.value())){
				continue ;
			} else
				result.append(word.value() + " ");
		}

		return result;
	}

}

// END_PUNCTUATION : )
class Word {
	private int type;
	private StringBuilder sb;

	private Word(int type, StringBuilder sb) {
		this.type = type;
		this.sb = sb;
	}

	final static Word create(int type, StringBuilder sb) {
		return new Word(type, sb);
	}

	public String toString() {
		return type + ":" + sb;
	}

	boolean isIgnoreType() {
		return isIgnoreType(type);
	}

	static boolean isIgnoreType(int type) {
		return type == Character.END_PUNCTUATION || type == Character.START_PUNCTUATION || type == Character.DIRECTIONALITY_WHITESPACE;
	}

	int type() {
		return type;
	}

	StringBuilder value() {
		return sb;
	}
}
