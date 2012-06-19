package net.ion.isearcher.indexer.report;

import net.ion.isearcher.events.IParserEventListener;
import net.ion.isearcher.events.ParserEvent;

public abstract class AbstractCollectListener implements ICollectListener, IParserEventListener {

	public void parsed(ParserEvent event) {
		collected(event);
	}
}
