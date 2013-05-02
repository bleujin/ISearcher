package net.ion.nsearcher.common;

import static net.ion.nsearcher.common.IKeywordField.ISKey;
import static net.ion.nsearcher.common.IKeywordField.TIMESTAMP;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;
import net.ion.framework.vfs.MyFileSystemManager;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class ReadDocument extends MyDocument {

	private static final long serialVersionUID = 2104871499687824141L;
	private Document doc;

	ReadDocument(Document doc) {
		this.doc = doc ;
	}

	public String docId(){
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
		return getField(name).stringValue() ;
	}

	public long getAsLong(String name) {
		return NumberUtil.toLong(getField(name).stringValue(), 0L);
	}

	@Override
	public Fieldable getField(String name) {
		if ( ArrayUtil.contains(IKeywordField.KEYWORD_FIELD, name)) {
			throw new IllegalArgumentException("["+ name + "] reserved field Id, use ReadDocuement.reserved Method.");
		} else {
			return doc.getFieldable(StringUtil.lowerCase(name)) ;
		}
	}

	public List<Fieldable> getFields() {
		final List<Fieldable> result = ListUtil.newList() ;
		
		for (String fieldName : getFieldNames()) {
			result.add(getField(fieldName)) ;
		}
		
		return Collections.unmodifiableList(result) ;
	}
	
	public Fieldable[] getFields(String name) {
		return doc.getFieldables(name) ;
	}

	private String[] getValues(String name) {
		return doc.getValues(name);
	}

	public void write(MyDocumentTemplate mw){
		mw.startDoc(this) ;
		List<Fieldable> fields = getFields() ;
		for (Fieldable field : fields) {
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
		for (Fieldable field : doc.getFields()) {
			if (IKeywordField.Field.reservedId(field.name())) continue ;
			if (field.name().endsWith(MyField.SORT_POSTFIX)) continue ;
			set.add(field.name()) ;
		}
		return set.toArray(new String[0]);
	}

	public Document toLuceneDoc() {
		return doc;
	}

	public String toString(){
		ToStringHelper helper = Objects.toStringHelper(this.getClass());
		for (String fieldName : getFieldNames()) {
			int i = 0 ;
			for (Fieldable field : getFields(fieldName)) {
				helper.add(fieldName + "[" + (i++) + "]", field.stringValue()).add("Indexed", field.isIndexed()).add("Stored", field.isStored()).add("Tokenized", field.isTokenized()) ;
			}
		}
		return helper.toString() ;
	}
	
}
