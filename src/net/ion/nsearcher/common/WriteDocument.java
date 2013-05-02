package net.ion.nsearcher.common;

import static net.ion.nsearcher.common.IKeywordField.ISALL_FIELD;
import static net.ion.nsearcher.common.IKeywordField.ISBody;
import static net.ion.nsearcher.common.IKeywordField.ISCollectorName;
import static net.ion.nsearcher.common.IKeywordField.ISEventName;
import static net.ion.nsearcher.common.IKeywordField.ISEventType;
import static net.ion.nsearcher.common.IKeywordField.ISKey;
import static net.ion.nsearcher.common.IKeywordField.TIMESTAMP;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.index.event.CollectorEvent;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class WriteDocument extends MyDocument {

	private static final long serialVersionUID = -8187265793444923069L;
	private String docId;
	private Action action = Action.Unknown;

	private ArrayListMultimap<String, MyField> fields = ArrayListMultimap.create() ;;
	WriteDocument(String docId) {
		this.docId = docId;
	}

	public String docId() {

		return docId;
	}

	public Document toLuceneDoc() {
		Document doc = new Document();
		StringBuilder bodyBuilder = new StringBuilder(docId + " ");

		for (MyField field : fields.values()) {
			if (field == null)
				continue;
			doc.add(field.indexField());
			for (Fieldable more : field.indexField().getMoreField()) {
				doc.add(more);
			}

			if (isReservedField(field.name()))
				continue;
			if (field.indexField().isIndexed() && field.indexField().isStored() && (!field.name().endsWith(MyField.SORT_POSTFIX))) {
				bodyBuilder.append(field.indexField().stringValue() + " ");
			}
		}

		doc.add(MyField.manual(ISKey, docId(), Store.YES, Index.NOT_ANALYZED).indexField());
		doc.add(MyField.manual(ISBody, String.valueOf(HashFunction.hashGeneral(bodyBuilder.toString())), Store.YES, Index.NOT_ANALYZED).indexField());
		doc.add(MyField.manual(TIMESTAMP, String.valueOf(System.currentTimeMillis()), Store.YES, Index.NOT_ANALYZED).indexField());

		// @TODO : compress, Store.No
		doc.add(MyField.manual(ISALL_FIELD, bodyBuilder.toString(), Store.NO, Index.ANALYZED).indexField());

		return doc;
	}
	
	
	public String bodyValue(){
		StringBuilder bodyBuilder = new StringBuilder(docId + " ");

		for (MyField field : fields.values()) {
			if (field == null)
				continue;
			if (isReservedField(field.name()))
				continue;
			if (field.indexField().isIndexed() && field.indexField().isStored() && (!field.name().endsWith(MyField.SORT_POSTFIX))) {
				bodyBuilder.append(field.indexField().stringValue() + " ");
			}
		}
		return String.valueOf(HashFunction.hashGeneral(bodyBuilder.toString())) ;
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


	public WriteDocument merge(MyField field) {
		removeField(field.name());
		return add(field);
	}

	public Fieldable getField(String name) {
		return getFirstField(name) ;
	}

	
	public WriteDocument add(MyField field) {
		fields.put(field.name(), field);
		return this;
	}

	public Collection<MyField> getFields() {
		return fields.values();
	}

	private Fieldable getFirstField(String _name){
		String name = StringUtil.lowerCase(_name) ;
		return fields.get(name).size() < 1 ? null : fields.get(name).get(0).indexField() ;  
	}
	
	public void removeField(String _name) {
		String name = StringUtil.lowerCase(_name) ;
		fields.removeAll(name);
	}

	public List<MyField> getFields(String _name) {
		String name = StringUtil.lowerCase(_name) ;
		return fields.get(name) ;
	}

}
