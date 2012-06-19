package net.ion.isearcher.searcher.processor;

import net.ion.isearcher.indexer.channel.RelayChannel;

public class ProcessorAdaptor implements PostProcessor {

	private RelayChannel<SearchTask> channel ;
	private ThreadProcessor threadProcessor ;
	public ProcessorAdaptor(RelayChannel<SearchTask> channel, ThreadProcessor threadProcessor) {
		this.channel = channel ;
		this.threadProcessor = threadProcessor ;
		
		threadProcessor.start() ;
	}

	public void postNotify(SearchTask searchTask) {
		channel.addMessage(searchTask) ;
	}
	
	

}
