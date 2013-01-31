package net.ion.nsearcher.common;


public interface IKeywordField {

	public final static String ISKey = "IS-Key";
	public final static String ISBody = "IS-Body";
	public final static String ISCollectorName = "IS-CollectorName";
	public final static String ISEventType = "IS-EventType";
	public final static String ISEventName = "IS-EventName";
	public final static String ISALL_FIELD = "IS-all";
	public final static String TIMESTAMP = "timestamp" ;

	public final static String[] KEYWORD_FIELD = new String[]{ISKey, ISBody, ISCollectorName, ISEventType, ISEventName, TIMESTAMP, ISALL_FIELD} ;

}
