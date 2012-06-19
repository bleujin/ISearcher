package net.ion.isearcher.searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.Debug;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.impl.ISearcher;

import org.apache.ecs.xml.XML;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class SearchResponse implements ISearchResponse {

	private ISearcher isearcher;
	private ISearchRequest request;
	private TopDocs docs;
	private long startTime;
	private long endTime;

	private SearchResponse(ISearcher isearcher, ISearchRequest request, TopDocs docs, long startTime) {
		this.isearcher = isearcher;
		this.request = request;
		this.docs = docs;
		this.startTime = startTime;
		this.endTime = System.currentTimeMillis();
	}

	public static SearchResponse create(ISearcher isearcher, ISearchRequest request, TopDocs docs, long startTime) {
		return new SearchResponse(isearcher, request, docs, startTime) ;
	}
	public int getTotalCount() {
		return docs.totalHits;
	}

	private List<MyDocument> getDocument(Page page) throws CorruptIndexException, IOException {
		ScoreDoc[] sdocs = docs.scoreDocs;
		List<MyDocument> result = new ArrayList<MyDocument>();

		for (int i = page.getStartLoc(); i < Math.min(page.getEndLoc(), sdocs.length); i++) {
			result.add(isearcher.doc(sdocs[i].doc));
		}
		return result;
	}

	public List<MyDocument> getDocument() throws CorruptIndexException, IOException {
		return getDocument(request.getPage());
	}

	public IDocumentSet allDocumentSet() throws IOException {
		List<MyDocument> result = new ArrayList<MyDocument>();
		ScoreDoc[] sdocs = docs.scoreDocs;
		for (int i = 0; i < sdocs.length; i++) {
			result.add(isearcher.doc(sdocs[i].doc));
		}
		return IDocumentSet.create(result);
	}

	public float getDocumentScore(int index) {
		return docs.scoreDocs[index].score;
	}

	public Explanation getDocumentExplain(int index) throws IOException, ParseException {
		return isearcher.getDocumentExplain(request, docs.scoreDocs[index].doc);
	}

	public ISearchRequest getRequest() {
		return request;
	}

	public ISearcher getSearcher() {
		return isearcher;
	}

	public long elapsedTime() {
		return endTime - startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public String toString() {
		return toXML().toString();
	}

	public XML toXML() {
		XML request = new XML("response");

		request.addAttribute("startTime", String.valueOf(startTime));
		request.addAttribute("elapsedTime", String.valueOf(elapsedTime()));
		request.addAttribute("totalCount", String.valueOf(getTotalCount()));

		return request;
	}

	public void debugPrint(Page page) throws CorruptIndexException, IOException {
		List<MyDocument> docs = getDocument(page);
		
		for (MyDocument doc : docs) {
			Debug.line(doc) ;
		}
	}
	
	public void each(Page page, Closure<MyDocument> clos) throws CorruptIndexException, IOException {
		CollectionUtil.each(getDocument(page), clos) ;
	}
	
}
