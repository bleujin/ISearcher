package net.ion.nsearcher.common;

import java.util.Map;

import net.ion.framework.util.ObjectUtil;

import org.apache.ecs.wml.Do;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public abstract class MyField {

	public final static String SORT_POSTFIX = "_for_sort";
	
	private float boost = 0.5f;
	private boolean ignoreBody; 

	public abstract void indexField(FieldIndexingStrategy strategy, Document doc) ;
	public abstract String name() ;
	public abstract String stringValue() ;

	public static MyField keyword(String name, String value) {
		return new KeywordField(name, value) ;
	}

	public static MyField number(String name, long value) {
		return new LongField(name, value) ;
	}
	
	public static MyField number(String name, double value) {
		return new DoubleField(name, value);
	}

	public static MyField date(String name, int yyyymmdd, int hh24miss){ 
		return new DateField(name, yyyymmdd, hh24miss);
	}

	public static MyField unknown(String name, Object value) {
		return new UnknownField(name, value);
	}

	public static MyField unknown(String name, String value) {
		return new UnknownStringField(name, value);
	}


	public static MyField manual(String name, String value, Store store, Index index){
		return new ManualField(name, value, store, index) ;
	}

	public static MyField text(String name, String value) {
		return new TextField(name, value);
	}
	
	public static MyField noStoreText(String name, String value) {
		return new NoStoreTextField(name, value);
	}
	public MyField setBoost(float boost) {
		this.boost = boost ;
		return this ;
	}
	
	public float boost(){
		return this.boost;
	}

	public boolean isIgnoreBody(){
		return ignoreBody ;
	}
	public MyField ignoreBody() {
		this.ignoreBody = true ;
		return this;
	}
}

class KeywordField extends MyField {
	private String name ;
	private String value ;
	
	KeywordField(String name, String value){
		this.name = name.toLowerCase() ;
		this.value = value ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.keyword(doc, this, name, value) ; 
	}
	public String name() {
		return name;
	}
	public String stringValue() {
		return ObjectUtil.toString(value) ;
	}
}

class LongField extends MyField {
	private String name ;
	private long value ;
	
	LongField(String name, long value){
		this.name = name.toLowerCase() ;
		this.value = value ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.number(doc, this, name, value); 
	}
	public String name() {
		return name;
	}
	public String stringValue() {
		return ObjectUtil.toString(value) ;
	}
}

class DoubleField extends MyField {
	private String name ;
	private double value ;
	DoubleField(String name, double value){
		this.name = name.toLowerCase() ;
		this.value = value ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.number(doc, this, name, value); 
	}
	public String name() {
		return name;
	}
	public String stringValue() {
		return ObjectUtil.toString(value) ;
	}
}

class DateField extends MyField {
	private String name ;
	private int yyyymmdd ;
	private int hh24miss ;
	DateField(String name, int yyyymmdd, int hh24miss){
		this.name = name.toLowerCase() ;
		this.yyyymmdd = yyyymmdd ;
		this.hh24miss = hh24miss ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.date(doc, this, name, yyyymmdd, hh24miss); 
	}
	public String name() {
		return name;
	}
	public String stringValue() {
		return ObjectUtil.toString(yyyymmdd + "-" + hh24miss) ;
	}
}

class UnknownField extends MyField {
	private String name ;
	private Object value ;
	UnknownField(String name, Object value){
		this.name = name.toLowerCase() ;
		this.value = value ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.unknown(doc, this, name, value); 
	}
	public String name() {
		return name;
	}
	public String stringValue() {
		return ObjectUtil.toString(value) ;
	}
}

class UnknownStringField extends MyField {
	private String name ;
	private String value ;
	UnknownStringField(String name, String value){
		this.name = name.toLowerCase() ;
		this.value = value ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.unknown(doc, this, name, value); 
	}
	public String name() {
		return name;
	}
	public String stringValue() {
//		return null ;
//		return value ;
		return ObjectUtil.toString(value) ;
	}
}

class ManualField extends MyField {
	private String name ;
	private String value ;
	private Store store;
	private Index index;
	ManualField(String name, String value, Store store, Index index){
		this.name = name ; // no lower
		this.value = value ;
		this.store = store ;
		this.index = index ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.manual(doc, name, value, store, index); 
	}
	public String name() {
		return name;
	}
	public String stringValue() {
		return ObjectUtil.toString(value) ;
	}

}

class TextField extends MyField {
	private String name ;
	private String value ;
	TextField(String name, String value){
		this.name = name.toLowerCase() ;
		this.value = value ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.text(doc, this, name, value);
	}
	
	public String name() {
		return name;
	}
	public String stringValue() {
		return ObjectUtil.toString(value) ;
	}
}

class NoStoreTextField extends MyField {
	private String name ;
	private String value ;
	NoStoreTextField(String name, String value){
		this.name = name.toLowerCase() ;
		this.value = value ;
	}
	public void indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.noStoreText(doc, this, name, value); 
	}
	public String name() {
		return name;
	}
	public String stringValue() {
		return ObjectUtil.toString(value) ;
	}
}
