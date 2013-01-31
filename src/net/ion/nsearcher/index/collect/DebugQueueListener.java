package net.ion.nsearcher.index.collect;

import java.util.LinkedList;

import net.ion.crawler.event.ParserEvent;
import net.ion.nsearcher.index.event.ICollectorEvent;
import net.ion.nsearcher.index.event.IParserEventListener;
import net.ion.nsearcher.index.event.ICollectorEvent.EventType;

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
