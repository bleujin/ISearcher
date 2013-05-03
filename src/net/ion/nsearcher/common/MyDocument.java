package net.ion.nsearcher.common;

import static net.ion.nsearcher.common.IKeywordField.KEYWORD_FIELD;

import java.io.Serializable;

import net.ion.framework.util.ObjectId;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.lucene.document.Document;

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