package net.ion.nsearcher.index.event;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.collect.ICollector;

public abstract class CollectorEvent implements ICollectorEvent{

	private static final long serialVersionUID = 1081027866981631957L;
	private long startTime ;
	public CollectorEvent(){
		startTime = System.currentTimeMillis() ;
	}
	
	public abstract ICollector getCollector() ;
	
	
	
	public EventType getEventType() {
		return EventType.Normal;
	}

	public WriteDocument[] makeDocument() throws IOException {
		return getCollector().getDocumentHandler().makeDocument(this) ;
	}
	
	public long getStartTime() {
		return startTime ;
	}

}
