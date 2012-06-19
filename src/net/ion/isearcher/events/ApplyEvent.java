package net.ion.isearcher.events;

import net.ion.isearcher.common.MyDocument;

public class ApplyEvent implements IIndexEvent{

	private MyDocument doc ;
	private long startTime ;
	public ApplyEvent(MyDocument doc){
		this.doc = doc ;
		this.startTime = System.currentTimeMillis() ;
	}
	public MyDocument getDocument() {
		return doc;
	}
	public String getDocumentField(String fieldName) {
		return getDocument().get(fieldName);
	}
	public long getStartTime() {
		return startTime ;
	}
	public String toString(){
		return doc.getName() ;
	}
}
