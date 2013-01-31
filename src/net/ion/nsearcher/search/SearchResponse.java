package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.MyDocument;

import org.apache.ecs.xml.XML;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class SearchResponse {

	private SearchRequest sreq;
	private SingleSearcher searcher;
	private TopDocs docs;
	private final long startTime;
	private Future<Void> postFuture ;
	
	private SearchResponse(SingleSearcher searcher, SearchRequest sreq, TopDocs docs, long startTime) {
		this.searcher = searcher ;
		this.sreq = sreq ;
		this.docs = docs ;
		this.startTime = startTime;
	}

	public static SearchResponse create(SingleSearcher searcher, SearchRequest sreq, TopDocs docs, long startTime) {
		return new SearchResponse(searcher, sreq, docs, startTime);
	}

	public int size() {
		return docs.totalHits ;
	}
	
	public SearchRequest request(){
		return sreq ;
	}

	public void debugPrint() throws IOException {
		debugPrint(Page.ALL) ;
	}

	public void debugPrint(Page page) throws IOException {
		List<MyDocument> docs = getDocument(page);
		
		for (MyDocument doc : docs) {
			Debug.line(doc) ;
		}
	}
	
	private List<MyDocument> getDocument(Page page) throws IOException {
		ScoreDoc[] sdocs = docs.scoreDocs;
		List<MyDocument> result = new ArrayList<MyDocument>();

		for (int i = page.getStartLoc(); i < Math.min(page.getEndLoc(), sdocs.length); i++) {
			result.add(searcher.doc(sdocs[i].doc));
		}
		return result;
	}

	public int getTotalCount() {
		return docs.totalHits;
	}

	public List<MyDocument> getDocument() throws IOException {
		ScoreDoc[] sdocs = docs.scoreDocs;
		List<MyDocument> result = new ArrayList<MyDocument>();

		for (int i = sreq.skip(); i < Math.min(sreq.offset(), sdocs.length); i++) {
			result.add(searcher.doc(sdocs[i].doc));
		}
		return result;
	}

	public long elapsedTime() {
		return 0L;
	}

	public XML toXML() {
		XML request = new XML("response");

		request.addAttribute("startTime", String.valueOf(startTime));
		request.addAttribute("elapsedTime", String.valueOf(elapsedTime()));
		request.addAttribute("totalCount", String.valueOf(getTotalCount()));

		return request;
	}

	public SearchResponse postFuture(Future<Void> postFuture) {
		this.postFuture = postFuture ;
		return this ;
	}

	public void awaitPostFuture() throws InterruptedException, ExecutionException {
		postFuture.get() ;
	}
	

}
