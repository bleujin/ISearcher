package net.ion.isearcher.indexer.channel;

import java.util.LinkedList;

import net.ion.isearcher.events.ICollectorEvent;
import net.ion.isearcher.events.IParserEventListener;
import net.ion.isearcher.events.ParserEvent;
import net.ion.isearcher.events.ICollectorEvent.EventType;
import net.ion.isearcher.indexer.report.ICollectListener;

public class DebugQueueListener implements IParserEventListener, ICollectListener {

	private LinkedList<ICollectorEvent> collects = new LinkedList<ICollectorEvent>();
	public void parsed(ParserEvent event) {
		if (! event.getEventType().equals(EventType.Normal)) return ;

		collects.add(event) ;
	}

	public void collected(ICollectorEvent event) {
		if (! event.getEventType().equals(EventType.Normal)) return ;

		collects.add(event) ;
	}
	
	public ICollectorEvent[] getCollectorEvents(){
		
		return (ICollectorEvent[])collects.toArray(new ICollectorEvent[0]) ;
	}

}
