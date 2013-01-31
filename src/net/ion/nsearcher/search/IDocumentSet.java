package net.ion.nsearcher.search;

import java.util.ArrayList;
import java.util.List;

import net.ion.nsearcher.common.MyDocument;

public class IDocumentSet {

	private List<MyDocument> docs ;
	private IDocumentSet(List<MyDocument> docs) {
		this.docs = docs ;
	}

	public static IDocumentSet create(List<MyDocument> docs) {
		return new IDocumentSet(docs);
	}

	
	public IDocumentSet applyFilter(IAfterDocumentFilter docFilter){
		List<MyDocument> result = new ArrayList<MyDocument>();
		for (MyDocument doc : docs) {
			if (docFilter.accept(doc)) result.add(doc);
		}
		return IDocumentSet.create(result);
	}
	
	public List<MyDocument> list(){
		return docs ;
	}
	
	public int size(){
		return docs.size() ;
	}
}
