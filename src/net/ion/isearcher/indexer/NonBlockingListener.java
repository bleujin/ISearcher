package net.ion.isearcher.indexer;

import net.ion.framework.util.Debug;
import net.ion.isearcher.events.CollectorEvent;
import net.ion.isearcher.events.ICollectorEvent;
import net.ion.isearcher.indexer.channel.RelayChannel;
import net.ion.isearcher.indexer.report.AbstractCollectListener;

public class NonBlockingListener extends AbstractCollectListener  {

	private final DefaultIndexer indexer ;
	private RelayChannel<ICollectorEvent> channel ;
	private Thread thread ;
	public NonBlockingListener(final DefaultIndexer runnable, RelayChannel<ICollectorEvent> channel){
		this.channel = channel ;
		this.indexer = runnable ;
		this.indexer.setChannel(channel) ;

		thread = new Thread(this.indexer, "Index Thread"){} ;
		thread.start() ;
	}

	
	public void collected(ICollectorEvent _event) {
		if (channel.isEndMessageOccured()) {
			if (_event instanceof CollectorEvent){
				Debug.error(channel.getCause()) ;
				((CollectorEvent)_event).getCollector().shutdown(channel.getCause()) ;
			}
		}
		channel.addMessage(_event);
	}


	public void joinIndexer() throws InterruptedException {
		thread.join() ;
	}
	
	public void restart(){
		thread.start() ;
	}

	public DefaultIndexer getDefaultIndexer(){
		return indexer ;
	}

}
