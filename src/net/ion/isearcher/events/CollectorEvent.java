package net.ion.isearcher.events;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.indexer.collect.ICollector;

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

	public MyDocument[] makeDocument() throws IOException {
		return getCollector().getDocumentHandler().makeDocument(this) ;
	}
	
	public long getStartTime() {
		return startTime ;
	}

}
