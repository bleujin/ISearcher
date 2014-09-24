package net.ion.nsearcher.common;

import static net.ion.nsearcher.common.IKeywordField.DocKey;
import static net.ion.nsearcher.common.IKeywordField.TIMESTAMP;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

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
		return reserved(DocKey) ;
	}

	public String reserved(String reservedId){
		if ( ArrayUtil.contains(IKeywordField.KEYWORD_FIELD, reservedId)) {
			return doc.get(reservedId) ;
		} else {
			throw new IllegalArgumentException("not reserved field Id : " + reservedId );
		}
	}
	
	public String asString(String name) {
		final IndexableField field = getField(name);
		if (field == null) return null ;
		return field.stringValue() ;
	}

	public String asString(String name, String defaultString) {
		return StringUtil.defaultIfEmpty(asString(name), defaultString) ;
	}


	public long asLong(String name, long dftValue) {
		return NumberUtil.toLong(getField(name).stringValue(), dftValue);
	}


	public IndexableField getField(String name) {
		if ( ArrayUtil.contains(IKeywordField.KEYWORD_FIELD, name)) {
			throw new IllegalArgumentException("["+ name + "] reserved field Id, use ReadDocuement.reserved Method.");
		} else {
			return doc.getField(name) ;
		}
	}

	public List<IndexableField> fields() {
		final List<IndexableField> result = ListUtil.newList() ;
		
		for (String fieldName : fieldNames()) {
			result.add(getField(fieldName)) ;
		}
		
		return Collections.unmodifiableList(result) ;
	}
	
	public IndexableField[] fields(String name) {
		return doc.getFields(name) ;
	}

	private String[] asStrings(String name) {
		return doc.getValues(name);
	}

	public void write(MyDocumentTemplate mw){
		mw.startDoc(this) ;
		List<IndexableField> fields = fields() ;
		for (IndexableField field : fields) {
			mw.printField(field) ;
		}
		mw.endDoc(this) ;
	}
	
	public long timestamp(){
		return Long.parseLong(reserved(TIMESTAMP)) ;
	}

	public String bodyValue() {
		return reserved(IKeywordField.BodyHash);
	}
	
	public String[] fieldNames() {
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
