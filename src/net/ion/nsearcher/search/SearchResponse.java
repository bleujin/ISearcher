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
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class SearchResponse {

	private SearchRequest sreq;
	private SingleSearcher searcher;
	private final long startTime;
	private final long endTime;
	private Future<Void> postFuture ;
	private List<MyDocument> docs ;
	private SearchResponse(SingleSearcher searcher, SearchRequest sreq, List<MyDocument> docs, long startTime) {
		this.searcher = searcher ;
		this.sreq = sreq ;
		this.startTime = startTime;
		this.endTime = System.currentTimeMillis();
		this.docs = docs ;
	}

	public static SearchResponse create(SingleSearcher searcher, SearchRequest sreq, TopDocs docs, long startTime) throws IOException {
		return new SearchResponse(searcher, sreq, makeDocument(searcher, sreq, docs), startTime);
	}

	public int totalCount() {
		return new Searcher(searcher).totalCount(sreq.resetClone(Integer.MAX_VALUE)) ;
	}
	
	public int size(){
		return docs.size() ;
	}
	
	public SearchRequest request(){
		return sreq ;
	}

	public void debugPrint() throws IOException {
		for (MyDocument doc : docs) {
			Debug.line(doc) ;
		}
	}
	
	public List<MyDocument> getDocument(){
		return docs ;
	}

	private static List<MyDocument> makeDocument(SingleSearcher searcher, SearchRequest sreq, TopDocs docs) throws IOException {
		ScoreDoc[] sdocs = docs.scoreDocs;
		List<MyDocument> result = new ArrayList<MyDocument>();

		for (int i = sreq.skip(); i < Math.min(sreq.limit(), sdocs.length); i++) {
			result.add(searcher.doc(sdocs[i].doc));
		}
		return result;
	}

	public long elapsedTime() {
		return endTime - startTime;
	}

	public XML toXML() {
		XML request = new XML("response");

		request.addAttribute("startTime", String.valueOf(startTime));
		request.addAttribute("elapsedTime", String.valueOf(elapsedTime()));
		request.addAttribute("totalCount", String.valueOf(totalCount()));
		request.addAttribute("size", String.valueOf(size()));

		return request;
	}

	public SearchResponse postFuture(Future<Void> postFuture) {
		this.postFuture = postFuture ;
		return this ;
	}

	public void awaitPostFuture() throws InterruptedException, ExecutionException {
		postFuture.get() ;
	}

	public long startTime() {
		return startTime;
	}
	

}
