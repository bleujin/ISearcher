package net.ion.nsearcher.search;

import java.io.IOException;

import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.ReadDocument;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;

public class DocHighlighter {

	private SearchResponse sr;
	private String savedFieldName;
	private Formatter formatter;
	private Highlighter highlighter;

	DocHighlighter(SearchResponse sr, String savedFieldName, String matchString) {
		this.sr = sr ;
		this.savedFieldName = savedFieldName ;
		
		this.formatter = new SimpleHTMLFormatter("<span class='matched'>","</span>");
		this.highlighter = new Highlighter(formatter, new QueryScorer(new TermQuery(new Term(savedFieldName, matchString))));
	}

	public DocHighlighter htmlFormatter(Formatter formatter){
		this.formatter = formatter ;
		return this ;
	}
	
	public DocHighlighter simpleHtmlFormatter(String prefix, String tail){
		return htmlFormatter(new SimpleHTMLFormatter(prefix, tail));
	}
	
	public String asString(ReadDocument doc) throws IOException, InvalidTokenOffsetsException{

		String savedText = doc.asString(savedFieldName) ;
		TokenStream tstream = TokenSources.getAnyTokenStream(sr.indexReader(), doc.docId(), savedFieldName, sr.searchConfig().queryAnalyzer());
		TextFragment[] tvfrag = highlighter.getBestTextFragments(tstream, savedText, false, 3);
		
		StringBuilder result = new StringBuilder() ;
		for (int j = 0; j < tvfrag.length; j++) {
			if ((tvfrag[j] != null) && (tvfrag[j].getScore() > 0)) {
				result.append(tvfrag[j].toString());
			}
		}
		return StringUtil.defaultIfEmpty(result.toString(), savedText);
	}

}
