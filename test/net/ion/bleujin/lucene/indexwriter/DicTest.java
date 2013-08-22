package net.ion.bleujin.lucene.indexwriter;


import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;

import org.apache.lucene.analysis.kr.AnalyzerUtil;

public class DicTest extends ISTestCase{

	public void testDic() throws Exception {
		
		String[] tokens = AnalyzerUtil.toToken(createKoreanAnalyzer(), "태극기가 바람에 펄럭입니다. 오영준 삼성신한생명LGU+보증보험");
		Debug.line(tokens) ;
		
	}
}
