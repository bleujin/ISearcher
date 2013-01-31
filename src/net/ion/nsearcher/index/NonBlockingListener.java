package net.ion.nsearcher.index;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.index.channel.RelayChannel;
import net.ion.nsearcher.index.event.CollectorEvent;
import net.ion.nsearcher.index.event.ICollectorEvent;
import net.ion.nsearcher.index.report.AbstractCollectListener;

public class NonBlockingListener extends AbstractCollectListener  {

	private final AsyncIndexer indexer ;
	private RelayChannel<ICollectorEvent> channel ;
	private Future<Void> future = null ;
	private CountDownLatch latch = new CountDownLatch(1) ;
	public NonBlockingListener(final AsyncIndexer runnable, RelayChannel<ICollectorEvent> channel){
		this.channel = channel ;
		this.indexer = runnable ;
		this.indexer.setChannel(channel) ;
	}
	
	public void collected(ICollectorEvent _event) {
		if (_event.getEventType().isBegin()){
			future = indexer.index() ;
			latch.countDown() ;
		}
		
		if (channel.isEndMessageOccured()) {
			if (_event instanceof CollectorEvent){
				Debug.error(channel.getCause()) ;
				((CollectorEvent)_event).getCollector().shutdown(channel.getCause()) ;
			}
		}
		channel.addMessage(_event);
	}

	public void waitForCompleted() throws ExecutionException, InterruptedException {
		if (future == null) { // not started
			latch.await() ;
		}
		future.get() ;
	}
	
	public AsyncIndexer getDefaultIndexer(){
		return indexer ;
	}

}
