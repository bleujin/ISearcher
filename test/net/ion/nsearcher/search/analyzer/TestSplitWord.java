package net.ion.nsearcher.search.analyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import org.apache.lucene.analysis.ko.MyKoreanAnalyzer.WordGroup;

public class TestSplitWord extends TestCase {

	public void testMakeWord() throws Exception {
		String str = "2000-12-3 abc 서울E플러스 펀드 SCH-B500 1(주식) 종류A 2000년 9월 30일 그 일이 일어났다. 4.19 의거 발생일  급락조짐을 보였으며 사과,배등이 살펴 보기에는 아마도 그럴것이다";
		Debug.line(WordGroup.testAdjustReader(str));
	}

	public void testReader() throws Exception {
		String str = "2000-12-3 abc 서울E플러스 펀드 SCH-B500 1(주식) 종류A 2000년 9월 30일 그 일이 일어났다. 4.19 의거 발생일  급락조짐을 보였으며 사과,배등이 살펴 보기에는 아마도 그럴것이다" ;
		
		ReusableStringReader reader = new ReusableStringReader();
		
		for (int i = 0 ; i < 1000000 ; i++) {
			reader.setValue(str) ;
			while(adjustReader2(reader).read() != -1);
		}
		
	}

	static Reader adjustReader2(Reader reader) {
		try {
			StringBuilder result = new StringBuilder(40);
			int pretype = 0;
			CharBuffer cb = CharBuffer.allocate(40);
			while (reader.read(cb) != -1) {
				for (char c : cb.array()) {
					int currtype = Character.getType(c);
					if (isSplit(pretype, currtype))
						result.append(' ');
					result.append(c);
					pretype = currtype;
				}
				cb.flip() ;
			}
			return new StringReader(result.toString());
		} catch (IOException ex) {
			ex.printStackTrace() ;
			return new StringReader("");
		}
	}

	static Reader adjustReader(Reader reader) {
//		return reader ;
		try {
			StringBuilder result = new StringBuilder();
			int pretype = 0;
			char[] cbuffr = new char[100];
			while (reader.read(cbuffr) != -1) {
				for (char c : cbuffr) {
					int currtype = Character.getType(c);
					if (isSplit(pretype, currtype))
						result.append(' ');
					result.append(c);
					pretype = currtype;
				}
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

class ReusableStringReader extends Reader {

	ReusableStringReader() {
		pos = 0;
		size = 0;
		s = null;
	}

	void setValue(String s) {
		this.s = s;
		size = s.length();
		pos = 0;
	}

	public int read() {
		if (pos < size) {
			return s.charAt(pos++);
		} else {
			s = null;
			return -1;
		}
	}

	public int read(char c[], int off, int len) {
		if (pos < size) {
			len = Math.min(len, size - pos);
			s.getChars(pos, pos + len, c, off);
			pos += len;
			return len;
		} else {
			s = null;
			return -1;
		}
	}

	public void close() {
		pos = size;
		s = null;
	}

	private int pos;
	private int size;
	private String s;
}