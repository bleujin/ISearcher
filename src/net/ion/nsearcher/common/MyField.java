package net.ion.nsearcher.common;

import java.util.Date;

import net.ion.framework.util.DateUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;

public class MyField {

	public enum MyFieldType {
		Keyword, Number, Double, Date, Text, Unknown, Byte
	}
	
	public final static String SORT_POSTFIX = "_for_sort";
	
	private boolean ignoreBody;

	private final Field ifield;
	private MyFieldType mtype; 

	public MyField(Field ifield, MyFieldType mtype){
		this.ifield = ifield ;
		this.mtype = mtype ;
	}
	
	public String name() {
		return ifield.name() ;
	}
	public String stringValue() {
		return ifield.stringValue() ;
	}
	
	public MyField boost(float boost){
		if(ifield.fieldType().indexed() && (!ifield.fieldType().omitNorms())) ifield.setBoost(boost);
		return this ;
	}
	
	public FieldType fieldType(){
		return ifield.fieldType() ;
	}
	
	public MyFieldType myFieldtype(){
		return mtype ;
	}

	public Field indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.save(doc, this, ifield) ;
		return ifield ;
	}

	public float boost(){
		return ifield.boost();
	}

	public boolean ignoreBody(){
		return ignoreBody ;
	}
	
	public MyField ignoreBody(boolean ignore) {
		this.ignoreBody = ignore ;
		return this;
	}
	

	
	
	public static MyField keyword(String name, String value) {
		return new MyField(new StringField(name, value, Store.YES), MyFieldType.Keyword) ;
	}

	public static MyField number(String name, long value) {
		return new MyField(new LongField(name, value, Store.YES), MyFieldType.Number) ;
	}
	
	public static MyField number(String name, double value) {
		return new MyField(new DoubleField(name, value, Store.YES), MyFieldType.Double);
	}

	public static MyField number(String name, float value) {
		return new MyField(new DoubleField(name, 1.0D * value, Store.YES), MyFieldType.Double);
	}

	public static MyField number(String name, int value) {
		return new MyField(new LongField(name, value, Store.YES), MyFieldType.Number);
	}

	public static MyField date(String name, Date date){ 
		return new MyField(new StringField(name, DateUtil.dateToString(date, "yyyyMMdd HHmmss"), Store.YES), MyFieldType.Date) ;
	}

	public static MyField date(String name, int yyyymmdd, int hh24miss){
		String ymd = String.valueOf(yyyymmdd) ;
		String hms = String.valueOf(hh24miss) ;
		Date date = new Date(NumberUtil.toInt(StringUtil.substring(ymd, 0, 4)) - 1900, 
				NumberUtil.toInt(StringUtil.substring(ymd, 4, 6))-1, 
				NumberUtil.toInt(StringUtil.substring(ymd, 6, 8)), 
				NumberUtil.toInt(StringUtil.substring(hms, 0, 2)), 
				NumberUtil.toInt(StringUtil.substring(hms, 2, 4)), 
				NumberUtil.toInt(StringUtil.substring(hms, 4, 6))) ;
		return date(name, date) ;
	}

	public static MyField text(String name, String value) {
		return new MyField(new TextField(name, value, Store.NO), MyFieldType.Text);
	}


	
	public static MyField keyword(String name, String value, Store store) {
		return new MyField(new StringField(name, value, store), MyFieldType.Keyword) ;
	}

	public static MyField number(String name, long value, Store store) {
		return new MyField(new LongField(name, value, store), MyFieldType.Number) ;
	}
	
	public static MyField number(String name, double value, Store store) {
		return new MyField(new DoubleField(name, value, store), MyFieldType.Number);
	}

	public static MyField number(String name, float value, Store store) {
		return new MyField(new FloatField(name, value, store), MyFieldType.Number);
	}

	public static MyField number(String name, int value, Store store) {
		return new MyField(new LongField(name, value, store), MyFieldType.Number);
	}

	public static MyField date(String name, Date date, Store store){ 
		return new MyField(new StringField(name, DateUtil.dateToString(date, "yyyyMMdd HHmmss"), store), MyFieldType.Date) ;
	}

	public static MyField text(String name, String value, Store store) {
		return new MyField(new TextField(name, value, store), MyFieldType.Text);
	}

	
	
	
	public static MyField unknown(String name, Long value) {
		return number(name, value) ;
	}
	public static MyField unknown(String name, Double value) {
		return number(name, value) ;
	}
	public static MyField unknown(String name, Float value) {
		return number(name, value) ;
	}
	public static MyField unknown(String name, Integer value) {
		return number(name, value) ;
	}
	public static MyField unknown(String name, Date value) {
		return date(name, value) ;
	}
	
	public static MyField unknown(String name, String value){
		if (StringUtil.isNotBlank(value) && StringUtil.isNumeric(value)) {
			return number(name, Long.parseLong(value)) ;
		} else if (StringUtil.isAlphanumericUnderbar(value)){
			return keyword(name, value) ;
		} else
			return new MyField(new TextField(name, value, Store.YES), MyFieldType.Unknown);
	}

	public static MyField manual(String name, String value, Store store, boolean analyze, MyFieldType fieldType){
		if (StringUtil.isAlphanumericUnderbar(value)){
			return keyword(name, value) ;
		} else
			return new MyField( analyze ? (new TextField(name, value.toString(), store)) : (new StringField(name, value.toString(), store)), fieldType);
	}


	public static MyField unknown(String name, Object value) {
		if (value == null){
			return keyword(name, "") ;
		}
		if (value.getClass().equals(Long.class)){
			return number(name, (Long)value) ;
		} else if (value.getClass().equals(Double.class)){
			return number(name, (Double)value) ;
		} else if(value.getClass().equals(Float.class)){
			return number(name, (Float)value) ;
		} else if(value.getClass().equals(Integer.class)){
			return number(name, (Integer)value) ;
		} else if(value.getClass().equals(Date.class)){
			return date(name, (Date)value) ;
		} else if (CharSequence.class.isInstance(value)) {
			return unknown(name, value.toString()) ;
		} else {
			return text(name, ObjectUtil.toString(value)) ;
		}
	}

	public static MyField manual(String name, Field ifield){
		return new MyField(ifield, MyFieldType.Unknown) ;
	}

	public static MyField noIndex(String name, String value) {
		return new MyField(new StoredField(name, value), MyFieldType.Text);
	}

	public static MyField noIndex(String name, long value) {
		return new MyField(new StoredField(name, value), MyFieldType.Number);
	}

	public static MyField noIndex(String name, int value) {
		return new MyField(new StoredField(name, value), MyFieldType.Number);
	}

	public static MyField noIndex(String name, double value) {
		return new MyField(new StoredField(name, value), MyFieldType.Number);
	}

	public static MyField noIndex(String name, float value) {
		return new MyField(new StoredField(name, value), MyFieldType.Number);
	}

	public static MyField noIndex(String name, byte[] value) {
		return new MyField(new StoredField(name, value), MyFieldType.Byte);
	}

	public static MyField noIndex(String name, BytesRef value) {
		return new MyField(new StoredField(name, value), MyFieldType.Byte);
	}
	

}

