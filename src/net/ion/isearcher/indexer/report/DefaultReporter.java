package net.ion.isearcher.indexer.report;

import java.util.concurrent.atomic.AtomicInteger;

import net.ion.framework.util.Debug;
import net.ion.isearcher.events.ICollectorEvent;

public class DefaultReporter extends AbstractCollectListener {

	private boolean print;
	private AtomicInteger count = new AtomicInteger() ;

	public DefaultReporter() {
		this(true);
	}

	public DefaultReporter(boolean print) {
		this.print = print;
	}

	public void collected(ICollectorEvent event) {
		if (print)
			Debug.debug(event);
		
		if (event.getEventType().isNormal()) {
			count.incrementAndGet() ;
		}
	}

	
	public int getCount(){
		return count.intValue() ;
	}
}
