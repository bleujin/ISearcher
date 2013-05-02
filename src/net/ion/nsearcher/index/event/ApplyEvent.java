package net.ion.nsearcher.index.event;

import net.ion.nsearcher.common.WriteDocument;

public class ApplyEvent implements IIndexEvent{

	private WriteDocument doc ;
	private long startTime ;
	public ApplyEvent(WriteDocument doc){
		this.doc = doc ;
		this.startTime = System.currentTimeMillis() ;
	}
	public WriteDocument getDocument() {
		return doc;
	}
	public String getDocumentField(String fieldName) {
		return getDocument().get(fieldName);
	}
	public long getStartTime() {
		return startTime ;
	}
	public String toString(){
		return doc.docId() ;
	}
}
