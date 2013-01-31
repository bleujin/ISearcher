package net.ion.nsearcher.index.report;

import net.ion.crawler.event.ParserEvent;
import net.ion.nsearcher.index.collect.ICollectListener;
import net.ion.nsearcher.index.event.IParserEventListener;

public abstract class AbstractCollectListener implements ICollectListener, IParserEventListener {

	public void parsed(ParserEvent event) {
		collected(event);
	}
}
