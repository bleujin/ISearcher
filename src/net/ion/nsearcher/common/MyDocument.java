package net.ion.nsearcher.common;

import static net.ion.nsearcher.common.IKeywordField.ISALL_FIELD;
import static net.ion.nsearcher.common.IKeywordField.ISBody;
import static net.ion.nsearcher.common.IKeywordField.ISCollectorName;
import static net.ion.nsearcher.common.IKeywordField.ISEventName;
import static net.ion.nsearcher.common.IKeywordField.ISEventType;
import static net.ion.nsearcher.common.IKeywordField.ISKey;
import static net.ion.nsearcher.common.IKeywordField.KEYWORD_FIELD;
import static net.ion.nsearcher.common.IKeywordField.TIMESTAMP;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.index.event.CollectorEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public final class MyDocument implements Serializable {

	private static final long serialVersionUID = -311310921909676445L;
	private static final String YET_NOT_DEFINED = "";
	
	public enum Action {
		Insert, Update, Delete, DeleteAll, Unknown;
		
		public boolean isInsert(){
			return this.equals(Insert) ;
		}
		public boolean isUpdate(){
			return this.equals(Update) ;
		}
		public boolean isDelete(){
			return this.equals(Delete) ;
		}
		public boolean isUnknown() {
			return this.equals(Unknown) ;
		}
	}
	private final Document doc ;
	private Action action = Action.Unknown;

	private final String docId ;
	private StringBuilder bodyBuilder ;
	
	private MyDocument(String docId, Document doc, StringBuilder bodyBuilder) {
		this.docId = docId ;
		this.doc = doc ;
		this.bodyBuilder = bodyBuilder;
	}
	
	public static MyDocument newDocument(String docId){
		return new MyDocument(docId, new Document(), new StringBuilder(docId + " ")) ;
	}
	
	public static MyDocument testDocument(){
		final String docId = new ObjectId().toString();
		return new MyDocument(docId, new Document(), new StringBuilder(docId + " ")) ;
	}

	public static MyDocument loadDocument(Document doc) {
		String docId = StringUtil.defaultIfEmpty(doc.get(ISKey), new ObjectId().toString());
		final String allBody = StringUtil.defaultIfEmpty(doc.get(IKeywordField.ISALL_FIELD), docId + " ");
		StringBuilder bodyBuilder = new StringBuilder(allBody) ;
		return new MyDocument(docId, doc, bodyBuilder) ;
	}
	
	
	public String docId(){
		return ObjectUtil.coalesce(docId, get(ISKey)) ;
	}

	
	public MyDocument name(String name){
		add(MyField.text(ISEventName, name)) ;
		return this ;
	}
	
	public MyDocument event(CollectorEvent event) throws IOException{
		add(MyField.manual(ISBody, String.valueOf(event.getEventBody()), Store.YES, Index.NOT_ANALYZED));
		add(MyField.text(ISCollectorName, event.getCollectorName()));
		add(MyField.text(ISEventType, event.getEventType().toString()));
		return this ;
	}
	
	public MyDocument add(Map<String, ? extends Object> values) {
		return add(JsonObject.fromObject(values));
	}

	public MyDocument add(JsonObject jso){
		recursiveField(this, "", jso) ;
		return this ;
	}
	
	private static void recursiveField(MyDocument mydoc, String prefix, JsonElement jso) {
		if (jso.isJsonPrimitive()){
			mydoc.add(MyField.unknown(StringUtil.defaultIfEmpty(prefix, "_root"), JsonUtil.toSimpleObject(jso))) ;
		} else if (jso.isJsonArray()) {
			JsonElement[] eles = jso.getAsJsonArray().toArray() ;
			for (JsonElement ele : eles) {
				recursiveField(mydoc, prefix, ele) ;
			}
		} else if (jso.isJsonObject()) {
			for (Entry<String, JsonElement> entry : jso.getAsJsonObject().entrySet() ) {
				if (StringUtil.isBlank(entry.getKey())) continue ;
				String fieldKey = StringUtil.isBlank(prefix) ? entry.getKey() : (prefix + "." + entry.getKey()) ;
				JsonElement value = entry.getValue() ;
				recursiveField(mydoc, fieldKey, value) ;
			}
			if (! StringUtil.isBlank(prefix)) mydoc.add(MyField.unknown(prefix, jso)) ;
		}
	}

	public String getIndexedDay(){
		return  DateUtil.timeMilliesToDay(Long.parseLong(get(TIMESTAMP))) ;
	}
	
	public MyDocument add(MyField field) {
		if (field == null) return this;
		doc.add(field);
		for (Fieldable more : field.getMoreField()) {
			doc.add(more) ;
		} 

		if( isReservedField(field.name())) return this ;
		if (field.isIndexed() && field.isStored() && (!field.name().endsWith(MyField.SORT_POSTFIX)) )  {
			bodyBuilder.append(field.stringValue() + " ") ;
		}

		return this ;
	}
	
	public MyDocument merge(MyField field){
		removeFields(field.name()) ;
		return add(field) ;
	}

	public String get(String name) {
		return doc.get(name) ;
	}

	public long getAsLong(String name) {
		return NumberUtil.toLong(get(name), 0L);
	}

	public Fieldable getField(String name) {
		return doc.getFieldable(name) ;
	}

	public List<Fieldable> getFields() {
		return doc.getFields() ;
	}

	public void write(MyDocumentTemplate mw){
		mw.startDoc(this) ;
		List<Fieldable> fields = getFields() ;
		for (Fieldable field : fields) {
			mw.printField(field) ;
		}
		mw.endDoc(this) ;
	}
	
	public Fieldable[] getFields(String name) {
		return doc.getFieldables(name) ;
	}

	public String[] getValues(String name) {
		return doc.getValues(name);
	}

	public void removeField(String name) {
		doc.removeField(name) ;
	}

	public void removeFields(String name) {
		doc.removeFields(name) ;
	}

	// Only Test
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String idValue() {
		return this.docId();
	}

	public String bodyValue() {
		return String.valueOf(HashFunction.hashGeneral(bodyBuilder.toString()));
	}

	
	public Document toLuceneDoc() {
		merge(MyField.manual(ISKey, docId(), Store.YES, Index.NOT_ANALYZED)) ;
		merge(MyField.manual(ISBody, bodyValue(), Store.YES, Index.NOT_ANALYZED));
		merge(MyField.manual(TIMESTAMP, String.valueOf(System.currentTimeMillis()), Store.YES, Index.NOT_ANALYZED));

		// @TODO : compress, Store.No
		merge(MyField.manual(ISALL_FIELD, bodyBuilder.toString() , Store.NO, Index.ANALYZED)) ;
		return doc;
	}

	static final boolean isReservedField(String fieldName){
		return ArrayUtils.contains(KEYWORD_FIELD, fieldName);
	}

	public void setAction(Action action){
		this.action = action ;
	}
	
	public Action getAction(){
		return this.action ;
	}
	

	public MyDocument keyword(String fieldName, String value) {
		add(MyField.keyword(fieldName, value)) ;
		return this;
	}

	public MyDocument text(String fieldName, String value) {
		add(MyField.text(fieldName, value)) ;
		return this;
	}

	public MyDocument number(String fieldName, long value) {
		add(MyField.number(fieldName, value)) ;
		return this;
	}

	public MyDocument addUnknown(String name, Object value) {
		add(MyField.unknown(name, value)) ;
		return this;
	}

	public String[] getFieldNames() {
		List<String> list = ListUtil.newList();
		for (Fieldable field : doc.getFields()) {
			list.add(field.name()) ;
		}
		return list.toArray(new String[0]);
	}


}
