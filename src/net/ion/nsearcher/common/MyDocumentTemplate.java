package net.ion.nsearcher.common;

import org.apache.lucene.document.Fieldable;



public interface MyDocumentTemplate {
	
	public void startDoc(ReadDocument doc)  ;

	public void printField(Fieldable field);

	public void endDoc(ReadDocument doc);

}