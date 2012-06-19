package net.ion.isearcher.searcher.processor;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.ion.framework.logging.LogBroker;
import net.ion.isearcher.indexer.channel.RelayChannel;

public abstract class ThreadProcessor extends Thread{

	private RelayChannel<SearchTask> channel ;
	private ThreadProcessor next ;
	private volatile boolean terminated = false;
	private Logger log = LogBroker.getLogger(ThreadProcessor.class);

	public ThreadProcessor(RelayChannel<SearchTask> channel) {
		this.channel = channel ;
	}
	
	public void setNext(ThreadProcessor next){
		if (this == next) throw new IllegalArgumentException("exception.search.same_processor") ;
		this.next = next ;
	}
	
	public final void run() {
		while (!terminated) {
			try {
				SearchTask stask = channel.pollMessage();
				support(stask);
			} catch (Exception ex) {
				log.log(Level.WARNING, "Task Not handled : " + ex.getMessage(), ex);
			}
		}
	}

    public final void support(SearchTask atask) {

        if (isDealWith(atask)){
            handle(atask) ;
        }

        if (next != null){
            next.support(atask);
        }
    }

	public final ThreadProcessor getNext() {
		return next;
	}
	
    protected abstract boolean isDealWith(SearchTask atask);
    protected abstract void handle(SearchTask atask);
	
	void stopProcessor() {
		terminated = true;
		interrupt();
	}

	public String toString() {
		StringBuilder str = new StringBuilder() ;
		str.append(this.getName() + "(" + this.getClass() + ")") ;
		if (this.next != null) {
			str.append("[" + this.next.toString()+ "]") ;
		}
		return str.toString() ;
	}
	
	public void restart() {
		this.terminated = false ;
		this.start() ;
		
	}
}
