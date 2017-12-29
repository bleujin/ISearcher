package org.apache.lucene.analysis.ko;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import net.ion.framework.util.ListUtil;

public class AnalyzerUtil {

	public static String[] toToken(Analyzer analyzer, String source) throws IOException {
		TokenStream tokenStream = analyzer.tokenStream("name", new StringReader(source));

		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);

		List<String> result = ListUtil.newList() ;
		
		tokenStream.reset();
		
		
		while (tokenStream.incrementToken()) {
			int start = offsetAttribute.startOffset();
			int end = offsetAttribute.endOffset();
			result.add(termAttribute.toString());
		}
		return result.toArray(new String[0]) ;
	}
}
