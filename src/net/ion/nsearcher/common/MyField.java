package net.ion.nsearcher.common;

import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class MyField {

	public final static String SORT_POSTFIX = "_for_sort";
	
	private final IndexField indexField ;
	private MyField(IndexField indexField) {
		this.indexField = indexField ;
	}

	public IndexField indexField(){
		return indexField ;
	}
	
	public String name(){
		return indexField.name() ;
	}
	
	public static MyField keyword(String name, String value) {
		return keyword(FieldIndexingStrategy.DEFAULT, name, value) ;
	}

	public static MyField keyword(FieldIndexingStrategy strategy, String name, String value) {
		return new MyField(strategy.keyword(name, value));
	}

	public static MyField number(String name, long value) {
		return number(FieldIndexingStrategy.DEFAULT, name, value) ;
	}

	public static MyField number(FieldIndexingStrategy strategy, String name, long value) {
		return new MyField(strategy.number(name, value));
	}

	public static MyField number(String name, double value) {
		return number(FieldIndexingStrategy.DEFAULT, name, value);
	}

	public static MyField number(FieldIndexingStrategy strategy, String name, double value) {
		return new MyField(strategy.number(name, value));
	}
	

	public static MyField date(String name, int yyyymmdd, int hh24miss){ 
		return date(FieldIndexingStrategy.DEFAULT, name, yyyymmdd, hh24miss);
	}

	public static MyField date(FieldIndexingStrategy strategy, String name, int yyyymmdd, int hh24miss) {
		return new MyField(strategy.date(name, yyyymmdd, hh24miss));
	}

	public static MyField unknown(String name, Object value) {
		return unknown(FieldIndexingStrategy.DEFAULT, name, value);
	}

	public static MyField unknown(FieldIndexingStrategy strategy, String name, Object value) {
		return new MyField(strategy.unknown(name, value));
	}

	public static MyField unknown(String name, String value) {
		return unknown(FieldIndexingStrategy.DEFAULT, name, value);
	}

	public static MyField unknown(FieldIndexingStrategy strategy, String name, String value) {
		return new MyField(strategy.unknown(name, value));
	}

	public static MyField manual(String name, String value, Store store, Index index){
		return manual(FieldIndexingStrategy.DEFAULT, name, value, store, index) ;
	}

	public static MyField manual(FieldIndexingStrategy strategy, String name, String value, Store store, Index index){
		return new MyField(strategy.manual(name, value, store, index)) ;
	}
	

	public static MyField text(String name, String value) {
		return text(FieldIndexingStrategy.DEFAULT, name, value);
	}
	
	public static MyField text(FieldIndexingStrategy strategy, String name, String value) {
		return new MyField(strategy.text(name, value));
	}

	public static MyField noStoreText(String name, String value) {
		return noStoreText(FieldIndexingStrategy.DEFAULT, name, value);
	}

	
	private static MyField noStoreText(FieldIndexingStrategy strategy, String name, String value) {
		return new MyField(strategy.noStoreText(name, value));
	}
	
}
