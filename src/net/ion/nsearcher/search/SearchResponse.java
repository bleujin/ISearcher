package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.ReadDocument;

import org.apache.ecs.xml.XML;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.google.common.base.Function;

public class SearchResponse {

	private SearchRequest sreq;
	private SingleSearcher searcher;
	private final long startTime;
	private final long endTime;
	private Future<Void> postFuture ;
	private List<Integer> docs ;
	private TopDocs tdocs;
	private SearchResponse(SingleSearcher searcher, SearchRequest sreq, List<Integer> docs, TopDocs tdocs, long startTime) {
		this.searcher = searcher ;
		this.sreq = sreq ;
		this.startTime = startTime;
		this.endTime = System.currentTimeMillis();
		this.docs = docs ;
		this.tdocs = tdocs ;
	}

	public static SearchResponse create(SingleSearcher searcher, SearchRequest sreq, TopDocs docs, long startTime) throws IOException {
		return new SearchResponse(searcher, sreq, makeDocument(searcher, sreq, docs), docs, startTime);
	}

	public int totalCount() {
		return tdocs.totalHits ;
//		return new Searcher(searcher, sreq.searcher().config()).totalCount(sreq.resetClone(Integer.MAX_VALUE)) ;
	}
	
	public int size(){
		return docs.size() ;
	}
	
	public SearchRequest request(){
		return sreq ;
	}

	public void debugPrint() throws IOException {
		eachDoc(EachDocHandler.DEBUG) ;
	}

	public <T> T eachDoc(EachDocHandler<T> handler){
		EachDocIterator iter = new EachDocIterator(searcher, sreq, docs) ;
		return handler.handle(iter) ;
	}
	
	
	public List<ReadDocument> getDocument() throws IOException{
		return eachDoc(EachDocHandler.TOLIST) ;
	}

	private static List<Integer> makeDocument(SingleSearcher searcher, SearchRequest sreq, TopDocs docs) {
		ScoreDoc[] sdocs = docs.scoreDocs;
		List<Integer> result = ListUtil.newList() ;

		for (int i = sreq.skip(); i < Math.min(sreq.limit(), sdocs.length); i++) {
			result.add(sdocs[i].doc);
		}
		return result;
	}
	
	public <T> T transformer(Function<TransformerKey, T> function){
		return function.apply(new TransformerKey(this.searcher, docs, sreq)) ;
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

	public void awaitPostFuture() throws InterruptedException, ExecutionException {
		postFuture.get() ;
	}

	public SearchResponse postFuture(Future<Void> postFuture) {
		this.postFuture = postFuture ;
		return this ;
	}


	

}
