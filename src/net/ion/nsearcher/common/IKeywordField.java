package net.ion.nsearcher.common;

import net.ion.framework.util.ArrayUtil;


public interface IKeywordField {

	public final static String DocKey = "IS-Key";
	public final static String BodyHash = "IS-Body";
	public final static String ISCollectorName = "IS-CollectorName";
	public final static String ISEventType = "IS-EventType";
	public final static String ISEventName = "IS-EventName";
	public final static String ISALL_FIELD = "IS-all";
	public final static String TIMESTAMP = "IS-Timestamp" ;

	public final static String[] KEYWORD_FIELD = new String[]{DocKey, BodyHash, ISCollectorName, ISEventType, ISEventName, TIMESTAMP, ISALL_FIELD} ;
	public final static String[] KEYWORD_MANDATORY_FIELD = new String[]{DocKey, BodyHash, TIMESTAMP} ;
	
	public final static class Field {
		
		public static boolean reservedId(String fieldName){
			return ArrayUtil.contains(KEYWORD_FIELD, fieldName) ;
		}
	} 

}
