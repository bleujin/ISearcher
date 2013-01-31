package net.ion.nsearcher.common;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.ion.framework.util.DateFormatUtil;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.FieldInfo.IndexOptions;

public class MyField implements Fieldable {

	private Fieldable field;
	private List<Fieldable> more = null;
	public final static String SORT_POSTFIX = "_for_sort";

	private MyField(Fieldable field) {
		this.field = field;
	}

	protected MyField(String name, String value, Field.Store store, Field.Index index) {
		this(new Field(name.toLowerCase(), value, store, index));
	}

	private static MyField create(String name, String value, Field.Store store, Field.Index index) {
		return new MyField(name, value, store, index);
	}

	public static MyField keyword(String name, String value) {
		MyField result = new MyField(name, value, Store.YES, Index.NOT_ANALYZED);

		result.addMoreField(sort(name, value));
		return result;
	}

	private static MyField sort(String name, String value) {
		return new MyField(makeSortFieldName(name), value, Store.YES, Index.NOT_ANALYZED);
	}

	public static String makeSortFieldName(String name) {
		return name + SORT_POSTFIX;
	}

	public static MyField number(String name, long number) {
		NumericField f = new NumericField(name, Store.YES, true); // number
		f.setLongValue(number);
		MyField result = new MyField(f);

		result.addMoreField(new Field(name, String.valueOf(number), Store.NO, Index.NOT_ANALYZED));

		NumericField sortfield = new NumericField(makeSortFieldName(name), 64, Store.YES, true); // sort
		sortfield.setLongValue(number);
		result.addMoreField(sortfield);

		return result;
	}

	public static MyField number(String name, double number) {
		NumericField f = new NumericField(name, Store.YES, true); // number
		f.setDoubleValue(number);
		MyField result = new MyField(f);

		result.addMoreField(new Field(name, String.valueOf(number), Store.NO, Index.NOT_ANALYZED));

		NumericField sortfield = new NumericField(makeSortFieldName(name), 64, Store.YES, true); // sort
		sortfield.setDoubleValue(number);
		result.addMoreField(sortfield);

		return result;
	}

	public static MyField date(String name, int yyyymmdd, int hh24miss) {
		MyField result = new MyField(name, yyyymmdd + " " + StringUtil.leftPad(String.valueOf(hh24miss), 6, '0'), Store.YES, Index.ANALYZED); // text
		// result.addMoreField(new MyField(name, yyyymmdd + "-" + hh24miss, Store.YES, Index.NOT_ANALYZED)) ; // keyword

		result.addMoreField(sort(name, yyyymmdd + "-" + StringUtil.leftPad(String.valueOf(hh24miss), 6, '0'))); // sort

		NumericField day = new NumericField(name, Store.YES, true);
		day.setLongValue(yyyymmdd);
		result.addMoreField(day);
		//
		NumericField datetime = new NumericField(name + "time", Store.YES, true) ;
		datetime.setLongValue(yyyymmdd * 1000000L + hh24miss) ;
		result.addMoreField(datetime) ;

		return result;
	}

	public static MyField unknown(String name, Object obj) {
		if (obj == null) {
			// throw new IllegalArgumentException("field value is not null") ;
			return text(name, "N/A");
		}

		if (obj instanceof Integer) {
			return number(name, (Integer)obj);
		} else if (obj instanceof Long) {
			return number(name, (Long)obj) ;
		} else if (obj instanceof Double) {
			return number(name, (Double)obj) ;
		} else if (obj instanceof Float) {
			return number(name, (Float)obj) ;
		} else if (obj instanceof Date) {
			// Debug.debug(name, "date") ;
			Date d = (Date) obj;
			String str = DateUtil.dateToString(d, DateUtil.DEFAULT_FORMAT);
			int yyyymmdd = Integer.parseInt(StringUtil.substringBefore(str, "-"));
			int hh24miss = Integer.parseInt(StringUtil.substringAfter(str, "-"));
			return date(name, yyyymmdd, hh24miss);
		} else {
			// Debug.debug(name, "text") ;
			return unknown(name, obj.toString());
		}
	}

	public static MyField unknown(String name, String value) {
		Date d = null;
		if (NumberUtil.isNumber(value)) {
			return MyField.number(name, NumberUtil.toLong(value));
		} else if (StringUtil.isAlphanumericUnderbar(value)) {
			return MyField.keyword(name, value);
		} else if ((d = DateFormatUtil.getDateIfMatchedType(value)) != null) {
			Calendar c = DateUtil.dateToCalendar(d);
			int yyyymmdd = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DATE);
			int hh24miss = c.get(Calendar.HOUR_OF_DAY) * 10000 + c.get(Calendar.MINUTE) * 100 + c.get(Calendar.SECOND);
			return MyField.date(name, yyyymmdd, hh24miss);
		} else {
			return MyField.text(name, value);
		}
	}

	public static MyField text(String name, String value) {
		String transValue = split(value) ;
		MyField result = new MyField(name, transValue, Store.YES, Index.ANALYZED);

		result.addMoreField(sort(name, StringUtil.substring(value, 0, 40)));

		return result;
	}
	
	private static String split(String value) {

//		StringUtil.split(value, " ,-:;")
//		
//		Debug.line(value) ;
//		if (true) return "서울 E플러스 펀드 SCH-B500 1(주식) 종류A 2000년 9월 30일 일이 일어났다. 4.19 의거 발생일  급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다" ;

		return value ; 
	}
	

	private void addMoreField(Fieldable field) {
		if (more == null)
			more = new ArrayList<Fieldable>();
		more.add(field);
	}

	private static MyField field(String name, String value, Store store, Index index) {
		return new MyField(name, value, store, index);
	}

	public static MyField noStoreText(String name, String value) {
		return new MyField(name, value, Store.NO, Index.ANALYZED);
	}

	public String toString() {
		return getRealField().toString();
	}

	public byte[] binaryValue() {
		return getRealField().getBinaryValue();
	}

	public int getBinaryLength() {
		return getRealField().getBinaryLength();
	}

	public int getBinaryOffset() {
		return getRealField().getBinaryOffset();
	}

	public byte[] getBinaryValue() {
		return getRealField().getBinaryValue();
	}

	public byte[] getBinaryValue(byte[] bytes) {
		return getRealField().getBinaryValue(bytes);
	}

	public float getBoost() {
		return getRealField().getBoost();
	}

	public boolean getOmitNorms() {
		return getRealField().getOmitNorms();
	}

	public boolean isBinary() {
		return getRealField().isBinary();
	}

	public boolean isIndexed() {
		return getRealField().isIndexed();
	}

	public boolean isLazy() {
		return getRealField().isLazy();
	}

	public boolean isStoreOffsetWithTermVector() {
		return getRealField().isStorePositionWithTermVector();
	}

	public boolean isStorePositionWithTermVector() {
		return getRealField().isStorePositionWithTermVector();
	}

	public boolean isStored() {
		return getRealField().isStored();
	}

	public boolean isTermVectorStored() {
		return getRealField().isTermVectorStored();
	}

	public boolean isTokenized() {
		return getRealField().isTokenized();
	}

	public String name() {
		return getRealField().name();
	}

	public Reader readerValue() {
		return getRealField().readerValue();
	}

	public void setBoost(float f) {
		getRealField().setBoost(f);
	}

	public void setOmitNorms(boolean flag) {
		getRealField().setOmitNorms(flag);
	}

	public String stringValue() {
		return getRealField().stringValue();
	}

	public TokenStream tokenStreamValue() {
		return field.tokenStreamValue();
	}

	public Fieldable getRealField() {
		return field;
	}

	public Fieldable[] getMoreField() {
		if (more == null || more.size() == 0)
			return new Fieldable[0];
		return more.toArray(new Fieldable[0]);
	}

	public IndexOptions getIndexOptions() {
		return field.getIndexOptions();
	}

	public void setIndexOptions(IndexOptions option) {
		field.setIndexOptions(option) ;
	}}
