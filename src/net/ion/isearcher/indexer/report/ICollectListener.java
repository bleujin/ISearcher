package net.ion.isearcher.indexer.report;

import java.util.EventListener;

import net.ion.isearcher.events.ICollectorEvent;


public interface ICollectListener extends EventListener{
	
	public void collected(ICollectorEvent event);

}
