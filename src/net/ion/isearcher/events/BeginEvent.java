package net.ion.isearcher.events;

import java.io.IOException;

import net.ion.isearcher.crawler.util.HashFunction;

public class BeginEvent implements ICollectorEvent {
	private static final long serialVersionUID = 8933347720640473244L;

	private String name ;
	private long startTime ;
	public BeginEvent(String name) {
		this.name = name ;
		this.startTime = System.currentTimeMillis() ;
	}

	public long getEventId() throws IOException {
		return HashFunction.hashGeneral(BeginEvent.class.toString());
	}

	public long getEventBody() {
		return HashFunction.hashGeneral(BeginEvent.class.toString());
	}

	public EventType getEventType() {
		return EventType.Begin;
	}

	public String getCollectorName() {
		return this.name;
	}
	
	public long getStartTime() {
		return startTime ;
	}
}
