package net.ion.nsearcher.search;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.ecs.xml.XML;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.config.SearchConfig;
import net.ion.nsearcher.extend.BaseSimilarity;
import net.ion.nsearcher.util.PageOption;

public class SearchResponse {

	private SearchRequest sreq;
	private ISearchable searcher;
	private final long startTime;
	private final long endTime;
	private Future<Void> postFuture;
	private List<Integer> docIds;
	private int totalCount;

	
	private SearchResponse(ISearchable searcher, SearchRequest sreq, List<Integer> docIds, int totalCount, long startTime) {
		this.searcher = searcher;
		this.sreq = sreq;
		this.startTime = startTime;
		this.endTime = System.currentTimeMillis();
		this.docIds = docIds;
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
		return docIds.size();
	}
	
	public List<Integer> docIds(){
		return docIds ;
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
		EachDocIterator iter = new EachDocIterator(searcher, sreq, docIds);
		return handler.handle(iter);
	}

	public List<ReadDocument> getDocument() throws IOException {
		return eachDoc(EachDocHandler.TOLIST);
	}

	
	public SearchResponse filter(Predicate<ReadDocument> predic) throws IOException{
		List<Integer> docIds = ListUtil.newList() ;
		for (ReadDocument rdoc : getDocument()) {
			if (predic.apply(rdoc)){
				docIds.add(rdoc.docId()) ;
			}
		}
		return new SearchResponse(searcher, sreq, docIds, docIds.size(), startTime) ;
	}
	

	public PageResponse getDocument(Page page) {
		List<Integer> result = ListUtil.newList();
		
		for (int i = page.getStartLoc(); i < Math.min(page.getEndLoc(), docIds.size()); i++) {
			result.add(docIds.get(i));
		}
		
		return PageResponse.create(this, result, page, docIds);
	}

	public ReadDocument documentById(int docId) throws IOException {
		return searcher.doc(docId, sreq) ;
	}

	public ReadDocument documentById(final String docIdValue) {
		return eachDoc(new EachDocHandler<ReadDocument>() {
			@Override
			public ReadDocument handle(EachDocIterator iter) {
				while (iter.hasNext()) {
					ReadDocument rdoc = iter.next();
					if (StringUtil.equals(docIdValue, rdoc.idValue()))
						return rdoc;
				}
				throw new IllegalArgumentException("not found doc : " + docIdValue);
			}
		});
	}
	
	public ReadDocument preDocBy(ReadDocument doc) throws IOException {
		for(int i = 1 ; i <docIds.size() ; i++){
			if (docIds.get(i) == doc.docId()) return documentById(docIds.get(i-1)) ;
		}
		return null ;
	}
	
	public ReadDocument nextDocBy(ReadDocument doc) throws IOException {
		for(int i = 0 ; i <docIds.size()-1 ; i++){
			if (docIds.get(i) == doc.docId()) return documentById(docIds.get(i+1)) ;
		}
		return null ;
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
		return function.apply(new TransformerKey(this.searcher, docIds, sreq));
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
	
	IndexReader indexReader() throws IOException {
		return searcher.indexReader();
	}

	SearchConfig searchConfig() {
		return searcher.searchConfig();
	}

	ISearchable searcher() {
		return searcher ;
	}

	public BaseSimilarity similarity(ReadDocument target) {
		return new BaseSimilarity(this, target);
	}


}
