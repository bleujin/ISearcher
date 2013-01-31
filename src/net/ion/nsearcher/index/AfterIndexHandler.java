package net.ion.nsearcher.index;

import net.ion.nsearcher.index.event.IIndexEvent;

public interface AfterIndexHandler {
	public void indexed(IIndexEvent ievent);
}
