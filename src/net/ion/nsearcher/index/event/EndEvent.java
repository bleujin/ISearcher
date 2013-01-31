package net.ion.nsearcher.index.event;

import java.io.IOException;

import net.ion.nsearcher.common.HashFunction;

public class EndEvent implements ICollectorEvent {
	private static final long serialVersionUID = -7622040264429566471L;

	private String name ;
	private long startTime; 
	public EndEvent(String name) {
		this.name = name ;
		this.startTime = System.currentTimeMillis() ;
	}

	public long getEventId() throws IOException {
		return HashFunction.hashGeneral(EndEvent.class.toString());
	}

	public long getEventBody() {
		return HashFunction.hashGeneral(EndEvent.class.toString());
	}

	public EventType getEventType() {
		return EventType.End;
	}

	public String getCollectorName() {
		return this.name;
	}
	
	public long getStartTime() {
		return startTime ;
	}	
}
