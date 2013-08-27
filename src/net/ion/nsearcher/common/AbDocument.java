package net.ion.nsearcher.common;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class AbDocument implements Serializable {

	private static final long serialVersionUID = -311310921909676445L;

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
	
	// Only Test
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public abstract String idValue() ;

}