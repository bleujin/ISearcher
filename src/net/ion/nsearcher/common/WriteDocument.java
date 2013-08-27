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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.index.event.CollectorEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

import com.google.common.collect.ArrayListMultimap;

public class WriteDocument extends AbDocument {

	private static final long serialVersionUID = -8187265793444923069L;
	private String docId;
	private Action action = Action.Unknown;

	private ArrayListMultimap<String, MyField> fields = ArrayListMultimap.create() ;;
	public WriteDocument(String docId) {
		this.docId = docId;
	}

	public String idValue() {

		return docId;
	}
	
	public Document toLuceneDoc(FieldIndexingStrategy strategy) {
		
		Document doc = new Document();
		StringBuilder bodyBuilder = new StringBuilder(1024);
		
		bodyBuilder.append(docId + " ") ;
		for (MyField field : fields.values()) {
			
			if (field == null)
				continue;
			final IndexField indexField = field.indexField(strategy);
			if (indexField == IndexField.BLANK) continue ;
			
			indexField.addTo(doc) ;

			if (isReservedField(field.name())) // except timestamp
				continue;

			bodyBuilder.append(field.stringValue() + " ");
		}

		doc.add(MyField.manual(ISKey, idValue(), Store.YES, Index.NOT_ANALYZED).indexField(strategy));
		final String bodyString = bodyBuilder.toString();
		doc.add(MyField.manual(ISBody, String.valueOf(HashFunction.hashGeneral(bodyString)), Store.YES, Index.NOT_ANALYZED).indexField(strategy));
		doc.add(MyField.manual(TIMESTAMP, String.valueOf(System.currentTimeMillis()), Store.YES, Index.NOT_ANALYZED).indexField(strategy));

		// @TODO : compress, Store.No
		doc.add(MyField.manual(ISALL_FIELD, bodyString, Store.NO, Index.ANALYZED).indexField(strategy));

		return doc;
	}
	
	
	public String bodyValue(){
		StringBuilder bodyBuilder = new StringBuilder(docId + " ");

		for (MyField field : fields.values()) {
			if (field == null)
				continue;
			if (isReservedField(field.name())) // except timestamp
				continue;
			bodyBuilder.append(field.stringValue() + " ");
		}
		return String.valueOf(HashFunction.hashGeneral(bodyBuilder.toString())) ;
	}
	
	private static final boolean isReservedField(String fieldName){
		return ArrayUtils.contains(KEYWORD_FIELD, fieldName);
	}
	

	public WriteDocument setAction(Action action) {
		this.action = action;
		return this ;
	}

	public Action getAction() {
		return this.action;
	}

	
	public String get(String name) {
		return getFirstField(name) == null ? null : getFirstField(name).stringValue() ;
	}

	public WriteDocument name(String name) {
		add(MyField.text(ISEventName, name));
		return this;
	}

	public WriteDocument event(CollectorEvent event) throws IOException {
		add(MyField.manual(ISBody, String.valueOf(event.getEventBody()), Store.YES, Index.NOT_ANALYZED));
		add(MyField.text(ISCollectorName, event.getCollectorName()));
		add(MyField.text(ISEventType, event.getEventType().toString()));
		return this;
	}

	public WriteDocument add(Map<String, ? extends Object> values) {
		return add(JsonObject.fromObject(values));
	}

	public WriteDocument add(JsonObject jso) {
		recursiveField(this, "", jso);
		return this;
	}

	private static void recursiveField(WriteDocument mydoc, String prefix, JsonElement jso) {
		if (jso.isJsonPrimitive()) {
			mydoc.add(MyField.unknown(StringUtil.defaultIfEmpty(prefix, "_root"), JsonUtil.toSimpleObject(jso)));
		} else if (jso.isJsonArray()) {
			JsonElement[] eles = jso.getAsJsonArray().toArray();
			for (JsonElement ele : eles) {
				recursiveField(mydoc, prefix, ele);
			}
		} else if (jso.isJsonObject()) {
			for (Entry<String, JsonElement> entry : jso.getAsJsonObject().entrySet()) {
				if (StringUtil.isBlank(entry.getKey()))
					continue;
				String fieldKey = StringUtil.isBlank(prefix) ? entry.getKey() : (prefix + "." + entry.getKey());
				JsonElement value = entry.getValue();
				recursiveField(mydoc, fieldKey, value);
			}
			if (!StringUtil.isBlank(prefix))
				mydoc.add(MyField.unknown(prefix, jso));
		}
	}

	public WriteDocument keyword(String fieldName, String value) {
		add(MyField.keyword(fieldName, value));
		return this;
	}

	public WriteDocument text(String fieldName, String value) {
		add(MyField.text(fieldName, value));
		return this;
	}

	public WriteDocument number(String fieldName, long value) {
		add(MyField.number(fieldName, value));
		return this;
	}

	public WriteDocument date(String fieldName, int yyyymmdd, int hh24miss) {
		add(MyField.date(fieldName, yyyymmdd, hh24miss));
		return this;
	}

	public WriteDocument unknown(String name, Object value) {
		add(MyField.unknown(name, value));
		return this;
	}

	public WriteDocument unknown(String name, String value) {
		add(MyField.unknown(name, value));
		return this;
	}


	public WriteDocument merge(MyField field) {
		removeField(field.name());
		return add(field);
	}

	public MyField myField(String name) {
		return getFirstField(name) ;
	}

	
	public WriteDocument add(MyField field) {
		fields.put(field.name(), field);
		return this;
	}

	public Collection<MyField> getFields() {
		return fields.values();
	}

	private MyField getFirstField(String _name){
		String name = StringUtil.lowerCase(_name) ;
		return fields.get(name).size() < 1 ? null : fields.get(name).get(0)  ;  
	}
	
	public void removeField(String name) {
		fields.removeAll(StringUtil.lowerCase(name));
	}

	public List<MyField> getFields(String name) {
		return fields.get(StringUtil.lowerCase(name)) ;
	}

}
