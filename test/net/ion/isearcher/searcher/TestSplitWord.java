package net.ion.isearcher.searcher;

import java.io.StringReader;
import java.util.List;
import java.util.Stack;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;

import junit.framework.TestCase;

public class TestSplitWord extends TestCase{

	public void testMakeWord() throws Exception {
		String str = "2000-12-3 abc 서울E플러스 펀드 SCH-B500 1(주식) 종류A 2000년 9월 30일 그 일이 일어났다. 4.19 의거 발생일  급락조짐을 보였으며 사과,배등이 살펴 보기에는 아마도 그럴것이다" ;
		Debug.line(WordGroup.testAdjustReader(str)) ;
	}
}
