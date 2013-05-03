package net.ion.nsearcher.search;

import java.util.ArrayList;
import java.util.List;

import net.ion.nsearcher.common.ReadDocument;

public class IDocumentSet {

	private List<ReadDocument> docs ;
	private IDocumentSet(List<ReadDocument> docs) {
		this.docs = docs ;
	}

	public static IDocumentSet create(List<ReadDocument> docs) {
		return new IDocumentSet(docs);
	}

	
	public IDocumentSet applyFilter(IAfterDocumentFilter docFilter){
		List<ReadDocument> result = new ArrayList<ReadDocument>();
		for (ReadDocument doc : docs) {
			if (docFilter.accept(doc)) result.add(doc);
		}
		return IDocumentSet.create(result);
	}
	
	public List<ReadDocument> list(){
		return docs ;
	}
	
	public int size(){
		return docs.size() ;
	}
}
