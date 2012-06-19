package net.ion.isearcher.indexer.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.ion.framework.util.Debug;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.events.CollectorEvent;
import net.ion.isearcher.events.ICollectorEvent;
import net.ion.isearcher.indexer.BeforeIndexHandler;

public class DefaultDocumentReporter  extends AbstractCollectListener {

	private List<BeforeIndexHandler> ibefores = new ArrayList<BeforeIndexHandler>();
	private AtomicInteger count = new AtomicInteger() ;

	public DefaultDocumentReporter() {
	}

	public void addIndexBeforeProcessor(BeforeIndexHandler ibefore) {
		ibefores.add(ibefore);
	}
	
	public void collected(ICollectorEvent event) {
		
		if (! event.getEventType().isNormal()) return ;
		
		try {
			
			
			MyDocument[] docs = ((CollectorEvent)event).makeDocument() ;
			for (MyDocument doc : docs) {
				handleBeforeProcessor((CollectorEvent) event, doc);
				count.incrementAndGet() ;
				Debug.debug(doc) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleBeforeProcessor(CollectorEvent event, MyDocument mydoc) {
		for (BeforeIndexHandler before : ibefores) {
			before.handleDoc(event, mydoc);
		}
	}
	
	public int getCount(){
		return count.intValue() ;
	}
}
