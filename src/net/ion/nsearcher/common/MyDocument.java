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

public abstract class MyDocument implements Serializable {

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

	public static WriteDocument newDocument(String docId){
		return new WriteDocument(docId) ;
	}
	
	public static WriteDocument testDocument(){
		final String docId = new ObjectId().toString();
		return new WriteDocument(docId) ;
	}

	public static ReadDocument loadDocument(Document doc) {
		return new ReadDocument(doc) ;
	}
	
	public abstract String docId()  ; 

	// Only Test
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String idValue() {
		return this.docId();
	}
	
	static final boolean isReservedField(String fieldName){
		return ArrayUtils.contains(KEYWORD_FIELD, fieldName);
	}

}