package net.ion.nsearcher.search;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.util.PageOption;
import net.ion.nsearcher.util.PageOutPut;

import org.apache.ecs.xml.XML;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;

import com.google.common.base.Function;

public class SearchResponse {

	private SearchRequest sreq;
	private ISearchable searcher;
	private final long startTime;
	private final long endTime;
	private Future<Void> postFuture;
	private List<Integer> docs;
	private int totalCount;

	private SearchResponse(ISearchable searcher, SearchRequest sreq, List<Integer> docs, int totalCount, long startTime) {
		this.searcher = searcher;
		this.sreq = sreq;
		this.startTime = startTime;
		this.endTime = System.currentTimeMillis();
		this.docs = docs;
		this.totalCount = totalCount;
	}

	public static SearchResponse create(ISearchable searcher, SearchRequest sreq, List<Integer> docs, int totalCount, long startTime) throws IOException {
		return new SearchResponse(searcher, sreq, makeDocument(sreq, docs), totalCount, startTime);
	}

	public static SearchResponse create(ISearchable searcher, SearchRequest sreq, TopDocs docs, long startTime) throws IOException {
		return new SearchResponse(searcher, sreq, makeDocument(sreq, docs), docs.totalHits, startTime);
	}

	public int totalCount() {
		return totalCount;
		// 전체 total은 searcherImpl이 구함.
		// return searcher.totalCount(sreq, sreq.getFilter()) ;
	}

	public int size() {
		return docs.size();
	}

	public SearchRequest request() {
		return sreq;
	}

	public void debugPrint() throws IOException {
		eachDoc(EachDocHandler.DEBUG);
	}

	public void debugPrint(final String... fields) throws IOException {
		eachDoc(new EachDocHandler<Void>() {

			@Override
			public <T> T handle(EachDocIterator iter) {
				while (iter.hasNext()) {
					ReadDocument next = iter.next();
					List list = ListUtil.newList();
					list.add(next.toString());
					for (String field : fields) {
						list.add(next.asString(field));
					}
					Debug.line(list.toArray(new Object[0]));
				}
				return null;
			}
		});
	}

	public <T> T eachDoc(EachDocHandler<T> handler) {
		EachDocIterator iter = new EachDocIterator(searcher, sreq, docs);
		return handler.handle(iter);
	}

	public List<ReadDocument> getDocument() throws IOException {
		return eachDoc(EachDocHandler.TOLIST);
	}

	public List<ReadDocument> getDocument(final Page page) {
		return eachDoc(new EachDocHandler<List<ReadDocument>>() {
			@Override
			public List<ReadDocument> handle(EachDocIterator iter) {
				return page.subList(iter) ;
			}
		}) ;
	}

	private static List<Integer> makeDocument(SearchRequest sreq, List<Integer> docs) {
		List<Integer> result = ListUtil.newList();

		for (int i = sreq.skip(); i < Math.min(sreq.limit(), docs.size()); i++) {
			result.add(docs.get(i));
		}
		return result;
	}

	private static List<Integer> makeDocument(SearchRequest sreq, TopDocs docs) {
		ScoreDoc[] sdocs = docs.scoreDocs;
		List<Integer> result = ListUtil.newList();

		for (int i = sreq.skip(); i < Math.min(sreq.limit(), sdocs.length); i++) {
			result.add(sdocs[i].doc);
		}
		return result;
	}

	public <T> T transformer(Function<TransformerKey, T> function) {
		return function.apply(new TransformerKey(this.searcher, docs, sreq));
	}

	public long elapsedTime() {
		return endTime - startTime;
	}

	public long startTime() {
		return startTime;
	}

	public XML toXML() {
		XML result = new XML("response");

		result.addAttribute("startTime", String.valueOf(startTime));
		result.addAttribute("elapsedTime", String.valueOf(elapsedTime()));
		result.addAttribute("totalCount", String.valueOf(totalCount()));
		result.addAttribute("size", String.valueOf(size()));

		return result;
	}

	public String toString() {
		return toXML().toString();
	}

	public void awaitPostFuture() throws InterruptedException, ExecutionException {
		postFuture.get();
	}

	public SearchResponse postFuture(Future<Void> postFuture) {
		this.postFuture = postFuture;
		return this;
	}

	public ReadDocument first() throws IOException {
		List<ReadDocument> result = getDocument();
		if (result.size() > 0)
			return result.get(0);
		return null;
	}

	public String pagePrint(Page page) {
		String result = PageOption.DEFAULT.toHtml(this.size(), page);
		return result;
	}

	
	public DocHighlighter createHighlighter(String savedFieldName, String matchString){
		return new DocHighlighter(this, savedFieldName, matchString) ;
	}
	
	

	public String asHighlight(ReadDocument doc, String savedFieldName, String matchString) throws IOException, InvalidTokenOffsetsException {
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span class='matched'>","</span>");
		Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(new TermQuery(new Term(savedFieldName, matchString))));
		
		String tvtext = doc.asString(savedFieldName) ;
		TokenStream tstream = TokenSources.getAnyTokenStream(searcher.indexReader(), doc.docId(), savedFieldName, searcher.searchConfig().queryAnalyzer());
		TextFragment[] tvfrag = highlighter.getBestTextFragments(tstream, tvtext, false, 10);
		
		StringBuilder result = new StringBuilder() ;
		for (int j = 0; j < tvfrag.length; j++) {
			if ((tvfrag[j] != null) && (tvfrag[j].getScore() > 0)) {
				result.append(tvfrag[j].toString());
			}
		}
		return result.toString();
	}

	IndexReader indexReader() throws IOException {
		return searcher.indexReader();
	}

	SearchConfig searchConfig() {
		return searcher.searchConfig();
	}
}
