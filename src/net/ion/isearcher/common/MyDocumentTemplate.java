package net.ion.isearcher.common;

import org.apache.lucene.document.Fieldable;

public interface MyDocumentTemplate {
	
	public void startDoc(MyDocument doc)  ;

	public void printField(Fieldable field);

	public void endDoc(MyDocument doc);

}
