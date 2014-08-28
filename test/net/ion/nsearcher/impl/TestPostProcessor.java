package net.ion.nsearcher.impl;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.index.channel.RelayChannel;
import net.ion.nsearcher.search.Searcher;
import net.ion.nsearcher.search.processor.LimitedChannel;
import net.ion.nsearcher.search.processor.ProcessorAdaptor;
import net.ion.nsearcher.search.processor.SearchTask;
import net.ion.nsearcher.search.processor.StdOutProcessor;
import net.ion.nsearcher.search.processor.ThreadProcessor;

public class TestPostProcessor extends ISTestCase{

	private Searcher searcher;
	public void setUp() throws Exception {
		searcher =  sampleTestDocument().newSearcher() ;
	}

	public void tearDown() throws Exception {
		
	}

	public void testInit() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		searcher.search("bleujin");
	}
	
	public void testMultiThreadPostProcessor() throws Exception {
		LimitedChannel<SearchTask> channel = new LimitedChannel<SearchTask>(100) ; 
		SampleThreadProcessor tp = new SampleThreadProcessor(channel);
		tp.setNext(new SampleThreadProcessor(channel)) ;
		
		searcher.addPostListener(new ProcessorAdaptor(channel, tp)) ;
		searcher.addPostListener(new StdOutProcessor()) ;
		
		searcher.search("bleujin");
	}
}


class SampleThreadProcessor extends ThreadProcessor {

	public SampleThreadProcessor(RelayChannel<SearchTask> channel) {
		super(channel);
	}

	protected void handle(SearchTask atask) {
		Debug.debug(atask) ;
	}

	@Override
	protected boolean isDealWith(SearchTask atask) {
		return true;
	}
	
}

