package net.ion.isearcher.indexer;

import net.ion.isearcher.events.IIndexEvent;

public interface AfterIndexHandler {
	public void indexed(IIndexEvent ievent);
}
