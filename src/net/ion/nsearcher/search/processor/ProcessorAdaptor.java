package net.ion.nsearcher.search.processor;

import net.ion.nsearcher.index.channel.RelayChannel;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;

public class ProcessorAdaptor implements PostProcessor {

	private RelayChannel<SearchTask> channel ;
	private ThreadProcessor threadProcessor ;
	public ProcessorAdaptor(RelayChannel<SearchTask> channel, ThreadProcessor threadProcessor) {
		this.channel = channel ;
		this.threadProcessor = threadProcessor ;
		
		threadProcessor.start() ;
	}

	public void postNotify(SearchRequest sreq, SearchResponse sres) {
		channel.addMessage(new SearchTask(sreq, sres)) ;
	}
	
	

}
