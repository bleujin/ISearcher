package net.ion.isearcher.indexer;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.events.CollectorEvent;

public interface BeforeIndexHandler {

	public void handleDoc(CollectorEvent event, MyDocument mydoc);

}
