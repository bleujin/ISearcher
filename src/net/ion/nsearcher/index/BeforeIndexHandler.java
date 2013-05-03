package net.ion.nsearcher.index;

import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.event.CollectorEvent;

public interface BeforeIndexHandler {

	public void handleDoc(CollectorEvent event, WriteDocument mydoc);

}
