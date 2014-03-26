package net.ion.nsearcher.common;

import static net.ion.nsearcher.common.IKeywordField.ISKey;
import static net.ion.nsearcher.common.IKeywordField.TIMESTAMP;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class ReadDocument extends AbDocument {

	private static final long serialVersionUID = 2104871499687824141L;
	private Document doc;

	ReadDocument(Document doc) {
		this.doc = doc ;
	}

	public static ReadDocument loadDocument(Document doc) {
		return new ReadDocument(doc) ;
	}
	
	public String idValue(){
		return reserved(ISKey) ;
	}

	public String reserved(String reservedId){
		if ( ArrayUtil.contains(IKeywordField.KEYWORD_FIELD, reservedId)) {
			return doc.get(reservedId) ;
		} else {
			throw new IllegalArgumentException("not reserved field Id : " + reservedId );
		}
	}
	
	public String get(String name) {
		final IndexableField field = getField(name);
		if (field == null) return null ;
		return field.stringValue() ;
	}

	public long getAsLong(String name) {
		return NumberUtil.toLong(getField(name).stringValue(), 0L);
	}


	public IndexableField getField(String name) {
		if ( ArrayUtil.contains(IKeywordField.KEYWORD_FIELD, name)) {
			throw new IllegalArgumentException("["+ name + "] reserved field Id, use ReadDocuement.reserved Method.");
		} else {
			return doc.getField(StringUtil.lowerCase(name)) ;
		}
	}

	public List<IndexableField> getFields() {
		final List<IndexableField> result = ListUtil.newList() ;
		
		for (String fieldName : getFieldNames()) {
			result.add(getField(fieldName)) ;
		}
		
		return Collections.unmodifiableList(result) ;
	}
	
	public IndexableField[] getFields(String name) {
		return doc.getFields(name) ;
	}

	private String[] getValues(String name) {
		return doc.getValues(name);
	}

	public void write(MyDocumentTemplate mw){
		mw.startDoc(this) ;
		List<IndexableField> fields = getFields() ;
		for (IndexableField field : fields) {
			mw.printField(field) ;
		}
		mw.endDoc(this) ;
	}
	
	public String getIndexedDay(){
		return DateUtil.timeMilliesToDay(Long.parseLong(get(TIMESTAMP))) ;
	}

	public String bodyValue() {
		return reserved(IKeywordField.ISBody);
	}
	
	public String[] getFieldNames() {
		Set<String> set = SetUtil.newSet() ;
		for (IndexableField field : doc.getFields()) {
			if (IKeywordField.Field.reservedId(field.name())) continue ;
			if (field.name().endsWith(MyField.SORT_POSTFIX)) continue ;
			set.add(field.name()) ;
		}
		return set.toArray(new String[0]);
	}
	
	public String[] getSortedFieldNames() {
		Set<String> set = SetUtil.newSet() ;
		for (IndexableField field : doc.getFields()) {
			if (! field.name().endsWith(MyField.SORT_POSTFIX)) continue ;
			set.add(field.name()) ;
		}
		return set.toArray(new String[0]);
	}
	
	

	public Document toLuceneDoc() {
		return doc;
	}
	
	public <T> T transformer(Function<ReadDocument, T> function){
		return function.apply(this) ;
	}

	public String toString(){
		ToStringHelper helper = Objects.toStringHelper(this.getClass());
		helper.addValue(idValue()) ;
		for (String sortName : getSortedFieldNames()) {
			String fieldName = StringUtil.substringBefore(sortName, MyField.SORT_POSTFIX);
			helper.add(fieldName, getField(fieldName)) ; //   + "[" + (fieldType.indexed() ? "I" : "") +  (fieldType.stored() ? "S" : "") +  (fieldType.tokenized() ? "T" : "") + "]"
		}
		return helper.toString() ;
	}
	
}
