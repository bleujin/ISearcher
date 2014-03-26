package net.ion.nsearcher.common;

import java.util.Date;

import net.ion.framework.util.DateUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public abstract class FieldIndexingStrategy {

	
	public static final FieldIndexingStrategy DEFAULT = new FieldIndexingStrategy() {
		
		public void keyword(Document doc, MyField field, String name, String value) {
			doc.add(createField(FieldType.Keyword, name, value, Store.YES, Index.NOT_ANALYZED));
			doc.add(sort(name, value));
		}

		public void number(Document doc, MyField field, String name, long number) {
			doc.add(new LongField(name, number, Store.YES)); // number
			doc.add(new Field(name, String.valueOf(number), Store.NO, Index.NOT_ANALYZED));
			doc.add(new StringField(makeSortFieldName(name), StringUtil.leftPad(String.valueOf(number), 20, '0'), Store.YES)); // sort
		}

		public void number(Document doc, MyField field, String name, double number) {
			doc.add(new DoubleField(name, number, Store.YES)); // number
			doc.add(new Field(name, String.valueOf(number), Store.NO, Index.NOT_ANALYZED));
			doc.add(new StringField(makeSortFieldName(name), StringUtil.leftPad(String.valueOf(number), 20, '0'), Store.YES)); // sort
		}

		public void date(Document doc, MyField field, String name, int yyyymmdd, int hh24miss) {
			doc.add(createField(FieldType.Date, name, yyyymmdd + " " + StringUtil.leftPad(String.valueOf(hh24miss), 6, '0'), Store.YES, Index.ANALYZED)); // text
			// result.addMoreField(new MyField(name, yyyymmdd + "-" + hh24miss, Store.YES, Index.NOT_ANALYZED)) ; // keyword

			doc.add(sort(name, yyyymmdd + "-" + StringUtil.leftPad(String.valueOf(hh24miss), 6, '0'))); // sort
			doc.add(new LongField(name, 1L * yyyymmdd, Store.YES));
			doc.add(new LongField(name + "time", yyyymmdd * 1000000L + hh24miss, Store.YES)) ;
		}
		
		public void text(Document doc, MyField field, String name, String value) {
			final Field textField = createField(FieldType.Text, name, value, Store.NO, Index.ANALYZED);
			textField.setBoost(field.boost()) ;
			doc.add(textField);
			doc.add(sort(name, StringUtil.substring(value, 0, 20)));
		}
		
		
		public void noStoreText(Document doc, MyField field, String name, String value) {
			doc.add(createField(FieldType.Text, name, value, Store.NO, Index.ANALYZED));
		}
		
		public void unknown(Document doc, MyField field, String name, Object obj){
//			String name = StringUtil.lowerCase(_name) ;
			if (obj == null) {
				return ;
			}

			if (obj instanceof Integer) {
				number(doc, field, name, (Integer)obj);
			} else if (obj instanceof Long) {
				number(doc, field, name, (Long)obj) ;
			} else if (obj instanceof Double) {
				number(doc, field, name, (Double)obj) ;
			} else if (obj instanceof Float) {
				number(doc, field, name, (Float)obj) ;
			} else if (obj instanceof Date) {
				// Debug.debug(name, "date") ;
				Date d = (Date) obj;
				String str = DateUtil.dateToString(d, DateUtil.DEFAULT_FORMAT);
				int yyyymmdd = Integer.parseInt(StringUtil.substringBefore(str, "-"));
				int hh24miss = Integer.parseInt(StringUtil.substringAfter(str, "-"));
				date(doc, field, name, yyyymmdd, hh24miss);
			} else {
				// Debug.debug(name, "text") ;
				unknown(doc, field, name, obj.toString());
			}

		}
		
		public void unknown(Document doc, MyField field, String name, String value) {
			Date d = null;
			if (NumberUtil.isNumber(value)) {
				number(doc, field, name, NumberUtil.toLong(value));
			} else if (StringUtil.isAlphanumericUnderbar(value)) {
				keyword(doc, field, name, value);
//			} else if (Character.isDigit(value.charAt(0)) && Character.isDigit(value.charAt(1)) && (d = DateFormatUtil.getDateIfMatchedType(value)) != null) {
//				Calendar c = DateUtil.dateToCalendar(d);
//				int yyyymmdd = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DATE);
//				int hh24miss = c.get(Calendar.HOUR_OF_DAY) * 10000 + c.get(Calendar.MINUTE) * 100 + c.get(Calendar.SECOND);
//				return date(name, yyyymmdd, hh24miss);
			} else {
				text(doc, field, name, value);
			}
		}
		
		
		public void manual(Document doc, String name, String value, Store store, Index index) {
			doc.add(createField(FieldType.Manual, name, value, store, index)) ;
		}

	};
	
	
	
	public enum FieldType {
		Keyword, Number, Date, Manual, Text
	}
	
	public abstract void keyword(Document doc, MyField field, String name, String value) ;
	public abstract void number(Document doc, MyField field, String name, long number) ;
	public abstract void number(Document doc, MyField field, String name, double number) ;
	public abstract void date(Document doc, MyField field, String name, int yyyymmdd, int hh24miss) ;
	public abstract void text(Document doc, MyField field, String name, String value) ;
	public abstract void noStoreText(Document doc, MyField field, String name, String value) ;
	public abstract void unknown(Document doc, MyField field, String name, Object obj) ;
	public abstract void unknown(Document doc, MyField field, String name, String value) ;
	public abstract void manual(Document doc, String name, String value, Store store, Index index) ;
	
	
	
	public Field createField(FieldType fieldType, String name, String value, Field.Store store, Field.Index index){
		return new Field(fieldType == FieldType.Manual ? name : name.toLowerCase(), value, store, index) ;
	}

//	public IndexField createField(Field field){
//		return new IndexField(field) ;
//	}

	protected final Field sort(String name, String value) {
		return new Field(makeSortFieldName(name), value, Store.YES, Index.NOT_ANALYZED) ;
	}

	
	public final static String makeSortFieldName(String name) {
		return (name + MyField.SORT_POSTFIX).toLowerCase();
	}

}
