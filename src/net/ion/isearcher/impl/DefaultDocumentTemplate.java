package net.ion.isearcher.impl;

import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeWriter;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocumentTemplate;

import org.apache.lucene.document.Fieldable;

public class DefaultDocumentTemplate implements MyDocumentTemplate{

	private RopeWriter rw ;
	public DefaultDocumentTemplate(RopeWriter rw){
		this.rw = rw ;
	}
	
	public Rope getRope(){
		return rw.getRope() ;
	}
	
	public void endDoc(MyDocument doc) {
		rw.write("</ul>") ;
		rw.write("</ul>") ; 
	}

	public void printField(Fieldable field) {
		rw.write("<li>", field.name(), "[" + getFieldOption(field) ,"] : ", field.stringValue(), "<br/>") ;
	}

	public void startDoc(MyDocument doc) {
		rw.write("<ul style='line-height: 12pt;'><li>", doc.getIdValue()) ;
		rw.write("<ul>") ;
	}

	private StringBuilder getFieldOption(Fieldable field) {
		StringBuilder sb = new StringBuilder() ;
		sb.append("St:").append(field.isStored() ? "T" : "F")  
				.append(",St:").append(field.isStored() ? "T" : "F")  
				.append(",To:").append(field.isTokenized() ? "T" : "F")  
				.append(",In:").append(field.isIndexed() ? "T" : "F")  
				.append(",Bi:").append(field.isBinary() ? "T" : "F") ;
		return sb ;
	}
}
