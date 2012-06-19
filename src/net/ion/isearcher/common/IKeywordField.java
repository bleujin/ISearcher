package net.ion.isearcher.common;

import net.ion.isearcher.indexer.handler.DocumentHandler;

public interface IKeywordField {

	public final static String ISKey = "IS-Key";
	public final static String ISBody = "IS-Body";
	public final static String ISCollectorName = "IS-CollectorName";
	public final static String ISEventType = "IS-EventType";
	public final static String ISEventName = "IS-EventName";
	public static final String ISALL_FIELD = "IS-all";

	public final static String[] KEYWORD_FIELD = new String[]{ISKey, ISBody, ISCollectorName, ISEventType, ISEventName, DocumentHandler.TIMESTAMP, ISALL_FIELD} ;

}
