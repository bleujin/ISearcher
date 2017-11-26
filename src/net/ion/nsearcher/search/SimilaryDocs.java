package net.ion.nsearcher.search;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.extend.SimilaryDoc;

public class SimilaryDocs {

	private SearchResponse searchResponse;
	private ReadDocument mydoc;
	private List<SimilaryDoc> result;

	public SimilaryDocs(SearchResponse searchResponse, ReadDocument mydoc, List<SimilaryDoc> result) {
		this.searchResponse = searchResponse ;
		this.mydoc = mydoc ;
		this.result = result ;
	}

	public void debugPrint() {
		for (SimilaryDoc similaryDoc : result) {
			Debug.line(similaryDoc);
		}
	}

	public SimilaryDocs limit(int i) {
		return new SimilaryDocs(searchResponse, mydoc, result.subList(0, Math.min(i, result.size()))) ;
	}

	public <T> T eachDoc(EachDocHandler<T> eachDocHandler) {
		List<Integer> docIds = ListUtil.newList() ;
		for (SimilaryDoc sd : result) {
			docIds.add(sd.docId()) ;
		}
		return eachDocHandler.handle(new EachDocIterator(searchResponse.searcher(), searchResponse.request(), docIds)) ;
	}

	public SimilaryDocs overWeight(double d) {
		List<SimilaryDoc> newList = ListUtil.newList() ;
		for (SimilaryDoc sd : result) {
			if (sd.simValue() >= d) newList.add(sd) ;
		}
		
		return new SimilaryDocs(searchResponse, mydoc, newList) ;
	}

}
