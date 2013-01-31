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
import java.util.ArrayList;
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
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.index.event.CollectorEvent;
import net.ion.nsearcher.index.event.ICollectorEvent.EventType;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
	private Document doc;
	private String name ;
	private boolean hasKey = false ;
	private Action action = Action.Unknown;
	private List<String> ignoreBodyField; 

	private MyDocument(Document doc, String name, boolean hasKey) {
		this.doc = doc;
		this.name = name ;
		this.hasKey = hasKey ;
		this.ignoreBodyField = new ArrayList<String>();
	}

	public static MyDocument newDocument(CollectorEvent event, String name) throws IOException {
		return newDocument(event, name, String.valueOf(event.getEventId()));
	}
	
	public static MyDocument newDocument(CollectorEvent event, String name, String docId) throws IOException {
		Document doc = new Document();
		doc.add(new Field(ISKey, docId, Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(ISBody, String.valueOf(event.getEventBody()), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(ISCollectorName, event.getCollectorName(), Store.YES, Index.ANALYZED));
		doc.add(new Field(ISEventType, event.getEventType().toString(), Store.YES, Index.ANALYZED));
		doc.add(new Field(ISEventName, name, Store.YES, Index.ANALYZED));
		doc.add(new Field(TIMESTAMP, String.valueOf(System.currentTimeMillis()), Store.YES, Index.NOT_ANALYZED)) ;
		
		return new MyDocument(doc, name, true);
	}

	public static MyDocument newDocument(String eventId, Map<String, ? extends Object> values){
		return newDocument(eventId, JsonObject.fromObject(values));
	}
	
	public static MyDocument newDocument(String eventId, JsonObject jso){
		Document doc = new Document();
		doc.add(new Field(ISKey, eventId, Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(ISBody, String.valueOf(jso.hashCode()), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(ISCollectorName, "unknown", Store.YES, Index.ANALYZED));
		doc.add(new Field(ISEventType, EventType.Normal.toString(), Store.YES, Index.ANALYZED));
		doc.add(new Field(ISEventName, eventId, Store.YES, Index.ANALYZED));
		doc.add(new Field(TIMESTAMP, String.valueOf(System.currentTimeMillis()), Store.YES, Index.NOT_ANALYZED)) ;
		
		final MyDocument mydoc = new MyDocument(doc, eventId, true);
		
		addField(mydoc, "", jso) ;
		return mydoc;
	}
	
	
	
	private static void addField(MyDocument mydoc, String prefix, JsonElement jso) {
		if (jso.isJsonPrimitive()){
			mydoc.add(MyField.unknown(StringUtil.defaultIfEmpty(prefix, "_root"), JsonUtil.toSimpleObject(jso))) ;
		} else if (jso.isJsonArray()) {
			JsonElement[] eles = jso.getAsJsonArray().toArray() ;
			for (JsonElement ele : eles) {
				addField(mydoc, prefix, ele) ;
			}
		} else if (jso.isJsonObject()) {
			for (Entry<String, JsonElement> entry : jso.getAsJsonObject().entrySet() ) {
				if (StringUtil.isBlank(entry.getKey())) continue ;
				String fieldKey = StringUtil.isBlank(prefix) ? entry.getKey() : (prefix + "." + entry.getKey()) ;
				JsonElement value = entry.getValue() ;
				addField(mydoc, fieldKey, value) ;
			}
			if (! StringUtil.isBlank(prefix)) mydoc.add(MyField.unknown(prefix, jso)) ;
		}
	}

	public static MyDocument testDocument() {
		Document doc = new Document();
		String keyString = new ObjectId().toString();
		doc.add(new Field(ISKey, keyString, Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(ISBody, "", Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(ISCollectorName, "unknown", Store.YES, Index.ANALYZED));
		doc.add(new Field(ISEventType, EventType.Normal.toString(), Store.YES, Index.ANALYZED));
		doc.add(new Field(TIMESTAMP, String.valueOf(System.currentTimeMillis()), Store.YES, Index.NOT_ANALYZED)) ;
		
		return new MyDocument(doc, keyString, true);
	}
	
	public String getDocId(){
		return get(ISKey) ;
	}
	public String getIndexedDay(){
		return  DateUtil.timeMilliesToDay(Long.parseLong(get(TIMESTAMP))) ;
	}
	
	public static MyDocument loadDocument(Document doc) {
		return new MyDocument(doc, doc.get(ISEventName), true) ;
	}
	
	public MyDocument add(MyField field) {
		if (field == null) return this;
		doc.add(field.getRealField());
		
		Fieldable[] more = field.getMoreField() ;
		for (Fieldable morefield : more) {
			doc.add(morefield);
		}
		return this ;
	}
	
	public MyDocument merge(MyField field){
		return add(field) ;
	}

//	public MyDocument add(Field field) {
//		if (field == null) return this;
//		doc.add(field);
//		return this ;
//	}

	public String get(String name) {
		return doc.get(name);
	}

	public long getAsLong(String name) {
		return NumberUtil.toLong(doc.get(name), 0L);
	}


	public Fieldable getField(String name) {
		return doc.getField(name);
	}

	public List<Fieldable> getFields() {
		List<Fieldable> result = ListUtil.newList() ;
		for (Fieldable f : doc.getFields()) {
			if (ArrayUtils.contains(IKeywordField.KEYWORD_FIELD, f.name()) || f.name().endsWith(MyField.SORT_POSTFIX) ) {
				continue ;
			}
			result.add(f) ;
		}
		
		return result;
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
		return doc.getFields(name);
	}

	public String[] getValues(String name) {
		return doc.getValues(name);
	}

	public void removeField(String name) {
		doc.removeField(name);
	}

	public void removeFields(String name) {
		doc.removeFields(name);
	}

	// Only Test
	public String toString() {
		return doc.toString();
	}

	public String getIdValue() {
		if (! hasKey) return YET_NOT_DEFINED ;
		return get(ISKey);
	}

	public String getBodyValue() {
		if (! hasKey) return YET_NOT_DEFINED ;
		return get(ISBody);
	}
	
	public void setIgnoreBodyField(List<String> ignoreBodyField) {
		this.ignoreBodyField = ignoreBodyField;
	}

	public Document toLuceneDoc() {
		removeField(ISALL_FIELD) ;
		StringBuilder keyBuilder = new StringBuilder() ;
		StringBuilder bodyBuilder = new StringBuilder() ;

		List<Fieldable> fields = getFields() ;
		
		for (Fieldable field : fields) {
			if( ignoreBodyField.contains(field.name())) continue ;
			if( isReservedField(field.name())) continue ;
			
			if (field.isStored() && !field.isTokenized()) keyBuilder.append(field.stringValue() + "/");  
			if (field.isIndexed() && (!field.name().endsWith(MyField.SORT_POSTFIX)) )  {
				bodyBuilder.append(field.stringValue() + " ") ;
			}
		}
		
		if (! hasKey){
			doc.add(new Field(ISKey, StringUtil.defaultIfEmpty(getIdValue(), String.valueOf(HashFunction.hashGeneral(keyBuilder.toString()))), Store.YES, Index.NOT_ANALYZED));
			doc.add(new Field(ISBody, String.valueOf(HashFunction.hashGeneral(bodyBuilder.toString())), Store.YES, Index.NOT_ANALYZED));
			hasKey = true ;
		}

		// @TODO : compress, Store.No
		doc.add(new Field(ISALL_FIELD, bodyBuilder.toString(), Store.NO, Index.ANALYZED)) ;
		return doc;
	}

	private final boolean isReservedField(String fieldName){
		return ArrayUtils.contains(KEYWORD_FIELD, fieldName);
	}

	public void setAction(Action action){
		this.action = action ;
	}
	
	public String getISKey(){
		return getField(ISKey).stringValue() ;
	}
	
	public Action getAction(){
		return this.action ;
	}
	
	public String getName(){
		return name ;
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


}
