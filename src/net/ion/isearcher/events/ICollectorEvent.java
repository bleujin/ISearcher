package net.ion.isearcher.events;

import java.io.IOException;

import net.ion.isearcher.common.IKeywordField;

public interface ICollectorEvent extends IKeywordField, IEvent{

	public enum EventType {
		Begin, End, Shutdown, Normal, Message ;
		
		public boolean isBegin(){
			return this.equals(Begin) ;
		}
		public boolean isEnd(){
			return this.equals(End) ;
		}
		public boolean isShutDown(){
			return this.equals(Shutdown) ;
		}
		public boolean isNormal() {
			return this.equals(Normal) ;
		}
		public boolean isMessage(){
			return this.equals(Message) ;
		}
	}
	
	public static String DIV = "_" ;

	long getEventId() throws IOException;
	long getEventBody() throws IOException ;
	EventType getEventType() ;
	String getCollectorName() ;
	long getStartTime() ;
}
