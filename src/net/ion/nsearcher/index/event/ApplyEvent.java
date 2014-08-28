package net.ion.nsearcher.index.event;

import net.ion.nsearcher.common.WriteDocument;

public class ApplyEvent implements IIndexEvent{

	private static final long serialVersionUID = 435638710291949640L;
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
		return getDocument().asString(fieldName);
	}
	public long getStartTime() {
		return startTime ;
	}
	public String toString(){
		return doc.idValue() ;
	}
}
