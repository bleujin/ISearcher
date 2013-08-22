package net.ion.nsearcher.common;

import org.apache.lucene.index.IndexableField;



public interface MyDocumentTemplate {
	
	public void startDoc(ReadDocument doc)  ;

	public void printField(IndexableField field);

	public void endDoc(ReadDocument doc);

}