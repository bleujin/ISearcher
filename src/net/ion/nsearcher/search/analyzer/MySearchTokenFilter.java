package net.ion.nsearcher.search.analyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.kr.KoreanFilter;

public class MySearchTokenFilter extends KoreanFilter {

	private TokenStream tokenStream = null;

	protected MySearchTokenFilter(TokenStream input) {
		super(input);
		tokenStream = input;
	}

//	public Token next() throws IOException {
//		Token token = tokenStream.next();
//		if (token == null) return null ;
//		
//		token = new Token(parse(token.term()), token.startOffset(), token.endOffset(), token.type());
//
//		return token;
//	}

	private String parse(String term) {
		return term.toString() ;
	}

}
