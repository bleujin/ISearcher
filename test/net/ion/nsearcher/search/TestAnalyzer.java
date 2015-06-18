package net.ion.nsearcher.search;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.processor.StdOutProcessor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.MyKoreanAnalyzer;
import org.apache.lucene.analysis.ko.morph.WordEntry;
import org.apache.lucene.analysis.ko.utils.DictionaryUtil;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class TestAnalyzer extends TestCase {

	public void testTokenStream() throws Exception {

		String think = "사람이 존재하는 이유는 생각하기 때문인가";
		Debug.debug(DictionaryUtil.existJosa("은"), DictionaryUtil.existEomi(think), DictionaryUtil.getAllNoun(think)) ;
		Debug.debug(DictionaryUtil.getAdverb(think), DictionaryUtil.getWord("사람"), DictionaryUtil.getVerb(think)) ;
		WordEntry word = DictionaryUtil.getWord("사람");
		Debug.debug(word, (word == null ? "" : word.getWord())) ;
	}
	
//	public void testAnalyzer() throws Exception {
//		long start = System.nanoTime() ;
//		MyKoreanAnalyzer anal = new MyKoreanAnalyzer();
//		String stmt = "재주_기예 사람 은존재한다. 서울e플러스펀드 SCH_B500 1(주식)종류A";
//		TokenStream tokenStream = anal.tokenStream("abc", new StringReader(stmt));
//		Debug.line((System.nanoTime() - start) / 1000000, "nano time") ;
//		
//		List<Token> store = new ArrayList<Token>() ;
//		Token t = null ;
//		
//		while((t = tokenStream.next()) != null){
//			if (t != null) store.add(t) ;
//		}
//		Collections.sort(store, new Comparator(){
//			public int compare(Object left, Object right) {
//				return ((Token)left).startOffset() -((Token)right).startOffset() ;
//			}
//		}) ;
//		
//		Debug.debug(store) ;
//		
//		assertEquals(true, findKeyword(true, anal, stmt, "e플러스")) ;
//		assertEquals(true, findKeyword(false, anal, stmt, "B500")) ;
//		assertEquals(true, findKeyword(false, anal, stmt, "SCH")) ;
//		assertEquals(true, findKeyword(false, anal, stmt, "SCH-B500")) ;
//		assertEquals(true, findKeyword(false, anal, stmt, "종류a")) ;
//	}
	
	
	
	public void testToken() throws Exception {
		Analyzer analyzer = new MyKoreanAnalyzer(Version.LUCENE_44, new CharArraySet(Version.LUCENE_44, ListUtil.toList("생각"), true)) ;// new CJKAnalyzer(Version.LUCENE_44, new CharArraySet(Version.LUCENE_44, ListUtil.toList("생각"), true)) ;
		TokenStream tokenStream = analyzer.tokenStream("text", "사람이 존재하는 이유는 생각하기 때문인가");
		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

		tokenStream.reset();
		while (tokenStream.incrementToken()) {
		    int startOffset = offsetAttribute.startOffset();
		    int endOffset = offsetAttribute.endOffset();
		    String term = charTermAttribute.toString();
		    Debug.line(startOffset, endOffset, term);
		}
		analyzer.close(); 
	}
	
	
	
	
	public boolean findKeyword(boolean printTerm, Analyzer anal, final String stmt, String term) throws Exception {
		Central c = CentralConfig.newRam().build() ;

		Indexer writer = c.newIndexer();
		writer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument();
				doc.add(MyField.text("name", stmt));
				isession.insertDocument(doc);
				return null;
			}
		}) ;


		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		Searcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		return searcher.createRequest(term, anal).find().size() > 0;
	}
	
	
	
	public void testPrintType() throws Exception {
		Debug.debug("COMBINING_SPACING_MARK", Character.COMBINING_SPACING_MARK) ;
		Debug.debug("CONNECTOR_PUNCTUATION", Character.CONNECTOR_PUNCTUATION) ;
		Debug.debug("CONTROL", Character.CONTROL) ;
		Debug.debug("CURRENCY_SYMBOL", Character.CURRENCY_SYMBOL) ;
		Debug.debug("DASH_PUNCTUATION", Character.DASH_PUNCTUATION) ; 
		Debug.debug("DECIMAL_DIGIT_NUMBER", Character.DECIMAL_DIGIT_NUMBER) ;
		Debug.debug("ENCLOSING_MARK", Character.ENCLOSING_MARK) ;
		Debug.debug("END_PUNCTUATION", Character.END_PUNCTUATION) ;
		Debug.debug("FINAL_QUOTE_PUNCTUATION", Character.FINAL_QUOTE_PUNCTUATION) ; 
		Debug.debug("FORMAT", Character.FORMAT) ;
		Debug.debug("INITIAL_QUOTE_PUNCTUATION", Character.INITIAL_QUOTE_PUNCTUATION) ;
		Debug.debug("LETTER_NUMBER", Character.LETTER_NUMBER) ;
		Debug.debug("LINE_SEPARATOR", Character.LINE_SEPARATOR) ;
		Debug.debug("LOWERCASE_LETTER", Character.LOWERCASE_LETTER) ; 
		Debug.debug("MATH_SYMBOL", Character.MATH_SYMBOL) ;
		Debug.debug("MODIFIER_LETTER", Character.MODIFIER_LETTER) ;
		Debug.debug("MODIFIER_SYMBOL", Character.MODIFIER_SYMBOL) ;
		Debug.debug("NON_SPACING_MARK", Character.NON_SPACING_MARK) ; 
		Debug.debug("OTHER_LETTER", Character.OTHER_LETTER) ; 
		Debug.debug("OTHER_NUMBER", Character.OTHER_NUMBER) ; 
		Debug.debug("OTHER_PUNCTUATION", Character.OTHER_PUNCTUATION) ;
		Debug.debug("OTHER_SYMBOL", Character.OTHER_SYMBOL) ;
		Debug.debug("PARAGRAPH_SEPARATOR", Character.PARAGRAPH_SEPARATOR) ;
		Debug.debug("PRIVATE_USE", Character.PRIVATE_USE) ;
		Debug.debug("SPACE_SEPARATOR", Character.SPACE_SEPARATOR) ;
		Debug.debug("START_PUNCTUATION", Character.START_PUNCTUATION ) ; 
		Debug.debug("SURROGATE", Character.SURROGATE) ; 
		Debug.debug("TITLECASE_LETTER", Character.TITLECASE_LETTER) ;
		Debug.debug("UNASSIGNED", Character.UNASSIGNED) ;
		Debug.debug("UPPERCASE_LETTER", Character.UPPERCASE_LETTER) ;
	}
	
}
