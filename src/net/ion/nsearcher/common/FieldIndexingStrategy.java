package net.ion.nsearcher.common;

import java.util.Calendar;
import java.util.Date;

import net.ion.framework.util.DateFormatUtil;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public abstract class FieldIndexingStrategy {

	
	public static final FieldIndexingStrategy DEFAULT = new FieldIndexingStrategy() {
		
		public IndexField keyword(String name, String value) {
			IndexField result = createField(FieldType.Keyword, name, value, Store.YES, Index.NOT_ANALYZED);

			result.addMoreField(sort(name, value));
			return result;
		}

		public IndexField number(String name, long number) {
			NumericField f = new NumericField(name, Store.YES, true); // number
			f.setLongValue(number);
			IndexField result = createField(f);

			result.addMoreField(new Field(name, String.valueOf(number), Store.NO, Index.NOT_ANALYZED));

			NumericField sortfield = new NumericField(makeSortFieldName(name), 64, Store.YES, true); // sort
			sortfield.setLongValue(number);
			result.addMoreField(sortfield);

			return result;
		}

		public IndexField number(String name, double number) {
			NumericField f = new NumericField(name, Store.YES, true); // number
			f.setDoubleValue(number);
			IndexField result = createField(f);

			result.addMoreField(new Field(name, String.valueOf(number), Store.NO, Index.NOT_ANALYZED));

			NumericField sortfield = new NumericField(makeSortFieldName(name), 64, Store.YES, true); // sort
			sortfield.setDoubleValue(number);
			result.addMoreField(sortfield);

			return result;
		}

		public IndexField date(String name, int yyyymmdd, int hh24miss) {
			IndexField result = createField(FieldType.Date, name, yyyymmdd + " " + StringUtil.leftPad(String.valueOf(hh24miss), 6, '0'), Store.YES, Index.ANALYZED); // text
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
		
		public IndexField text(String name, String value) {
			String transValue = split(value) ;
			IndexField result = createField(FieldType.Text, name, transValue, Store.YES, Index.ANALYZED);

			result.addMoreField(sort(name, StringUtil.substring(value, 0, 40)));

			return result;
		}
		
		
		public IndexField noStoreText(String name, String value) {
			return createField(FieldType.Text, name, value, Store.NO, Index.ANALYZED);
		}
		
		public IndexField unknown(String _name, Object obj){
			String name = StringUtil.lowerCase(_name) ;
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
		
		public IndexField unknown(String name, String value) {
			Date d = null;
			if (NumberUtil.isNumber(value)) {
				return number(name, NumberUtil.toLong(value));
			} else if (StringUtil.isAlphanumericUnderbar(value)) {
				return keyword(name, value);
			} else if (Character.isDigit(value.charAt(0)) && Character.isDigit(value.charAt(1)) && (d = DateFormatUtil.getDateIfMatchedType(value)) != null) {
				Calendar c = DateUtil.dateToCalendar(d);
				int yyyymmdd = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DATE);
				int hh24miss = c.get(Calendar.HOUR_OF_DAY) * 10000 + c.get(Calendar.MINUTE) * 100 + c.get(Calendar.SECOND);
				return date(name, yyyymmdd, hh24miss);
			} else {
				return text(name, value);
			}
		}
		
		
		public IndexField manual(String name, String value, Store store, Index index) {
			return createField(FieldType.Manual, name, value, store, index) ;
		}

	};
	
	
	
	public enum FieldType {
		Keyword {
			public IndexField toIndexField(FieldIndexingStrategy strategy, String name, String value){
				return strategy.keyword(name, value) ;
			} 
		}, Number, Date, Manual, Text
	}
	
	public abstract IndexField keyword(String name, String value) ;
	public abstract IndexField number(String name, long number) ;
	public abstract IndexField number(String name, double number) ;
	public abstract IndexField date(String name, int yyyymmdd, int hh24miss) ;
	public abstract IndexField text(String name, String value) ;
	public abstract IndexField noStoreText(String name, String value) ;
	public abstract IndexField unknown(String name, Object obj) ;
	public abstract IndexField unknown(String name, String value) ;
	public abstract IndexField manual(String name, String value, Store store, Index index) ;
	
	
	public IndexField createField(FieldType fieldType, String name, String value, Field.Store store, Field.Index index){
		return new IndexField(fieldType, name, value, store, index) ;
	}

	public IndexField createField(Fieldable field){
		return new IndexField(field) ;
	}

	protected final IndexField sort(String name, String value) {
		return new IndexField(FieldType.Keyword, makeSortFieldName(name), value, Store.YES, Index.NOT_ANALYZED);
	}

	
	public final static String makeSortFieldName(String name) {
		return name + MyField.SORT_POSTFIX;
	}
	
	protected String split(String value) {

//		StringUtil.split(value, " ,-:;")
//		
//		Debug.line(value) ;
//		if (true) return "서울 E플러스 펀드 SCH-B500 1(주식) 종류A 2000년 9월 30일 일이 일어났다. 4.19 의거 발생일  급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다" ;

		return value ; 
	}

}
