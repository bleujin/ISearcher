package net.ion.nsearcher.search.analyzer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ko.AnalyzerUtil;
import org.apache.lucene.analysis.miscellaneous.PatternAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class TestRegularAnalyzer extends TestCase {

	private static final Version TEST_VERSION_CURRENT = Version.LUCENE_4_10_2 ;

	public void testPattern() throws Exception {
		Pattern pattern = Pattern.compile("(.*)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher("bleujin");

		while (matcher.find()) {
			Debug.line(matcher.group());
		}
	}

	public void testFirst() throws Exception {
		Pattern pattern = Pattern.compile("-");
		Analyzer anal = anal = new PatternAnalyzer(SearchConstant.LuceneVersion, pattern, true, CharArraySet.EMPTY_SET);

		String[] tokens = AnalyzerUtil.toToken(anal, "KBS-345");
		Debug.line(tokens);
	}

	public void testNonWordPattern() throws IOException {
		PatternAnalyzer a = new PatternAnalyzer(TEST_VERSION_CURRENT, PatternAnalyzer.NON_WORD_PATTERN, false, null);
		Debug.line(AnalyzerUtil.toToken(a, "The quick brown Fox,the abcd1234 (56.78) dc.")) ;
	}
}
