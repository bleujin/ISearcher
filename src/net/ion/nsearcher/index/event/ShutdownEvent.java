package net.ion.nsearcher.index.event;

import java.io.IOException;

import net.ion.nsearcher.common.HashFunction;

public class ShutdownEvent implements ICollectorEvent {
	private static final long serialVersionUID = -2657460279618587644L;

	private String name ;
	private String cause ;
	private long startTime ; 
	public ShutdownEvent(String name, String cause) {
		this.name = name ;
		this.cause = cause ;
		this.startTime = System.currentTimeMillis() ;
	}

	public long getEventId() throws IOException {
		return HashFunction.hashGeneral(ShutdownEvent.class.toString());
	}

	public long getEventBody() {
		return HashFunction.hashGeneral(ShutdownEvent.class.toString());
	}

	public EventType getEventType() {
		return EventType.Shutdown;
	}

	public String getCollectorName() {
		return this.name;
	}
	
	public String toString(){
		return "Collecter Shutdown" + "[" + getCollectorName() + "], cause:" + cause ;
	}
	
	public long getStartTime() {
		return startTime ;
	}	
}
