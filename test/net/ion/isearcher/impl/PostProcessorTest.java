package net.ion.isearcher.impl;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.indexer.channel.RelayChannel;
import net.ion.isearcher.searcher.processor.LimitedChannel;
import net.ion.isearcher.searcher.processor.ProcessorAdaptor;
import net.ion.isearcher.searcher.processor.SearchTask;
import net.ion.isearcher.searcher.processor.StdOutProcessor;
import net.ion.isearcher.searcher.processor.ThreadProcessor;

import org.apache.lucene.store.Directory;

public class PostProcessorTest extends ISTestCase{

	private ISearcher searcher;
	public void setUp() throws Exception {
		Directory dir = writeDocument() ;
		searcher =  Central.createOrGet(dir).newSearcher();
	}

	public void tearDown() throws Exception {
		searcher.forceClose();
	}

	public void testInit() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		searcher.searchTest("bleujin");
	}
	
	public void testMultiThreadPostProcessor() throws Exception {
		LimitedChannel<SearchTask> channel = new LimitedChannel<SearchTask>(100) ; 
		SampleThreadProcessor tp = new SampleThreadProcessor(channel);
		tp.setNext(new SampleThreadProcessor(channel)) ;
		ProcessorAdaptor adaptor = new ProcessorAdaptor(channel, tp) ;
		
		searcher.addPostListener(adaptor) ;
		searcher.addPostListener(new StdOutProcessor()) ;
		
		searcher.searchTest("bleujin");
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

