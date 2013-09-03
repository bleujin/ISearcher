package net.ion.nsearcher.common;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;


public abstract class ManualIndexingStrategy extends FieldIndexingStrategy{

	@Override
	public void date(Document doc, MyField field, String name, int yyyymmdd, int hh24miss) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyword(Document doc, MyField field, String name, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noStoreText(Document doc, MyField field, String name, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void number(Document doc, MyField field, String name, long number) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void number(Document doc, MyField field, String name, double number) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void text(Document doc, MyField field, String name, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unknown(Document doc, MyField field, String name, Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unknown(Document doc, MyField field, String name, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void manual(Document doc, String name, String value, Store store, Index index) {
		// TODO Auto-generated method stub
		
	}



}
