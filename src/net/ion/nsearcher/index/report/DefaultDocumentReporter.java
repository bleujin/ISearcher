package net.ion.nsearcher.index.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.BeforeIndexHandler;
import net.ion.nsearcher.index.event.CollectorEvent;
import net.ion.nsearcher.index.event.ICollectorEvent;

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
			
			
			WriteDocument[] docs = ((CollectorEvent)event).makeDocument() ;
			for (WriteDocument doc : docs) {
				handleBeforeProcessor((CollectorEvent) event, doc);
				count.incrementAndGet() ;
				Debug.debug(doc) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleBeforeProcessor(CollectorEvent event, WriteDocument mydoc) {
		for (BeforeIndexHandler before : ibefores) {
			before.handleDoc(event, mydoc);
		}
	}
	
	public int getCount(){
		return count.intValue() ;
	}
}
