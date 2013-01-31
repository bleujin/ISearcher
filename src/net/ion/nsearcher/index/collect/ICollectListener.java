package net.ion.nsearcher.index.collect;

import java.util.EventListener;

import net.ion.nsearcher.index.event.ICollectorEvent;


public interface ICollectListener extends EventListener{
	
	public void collected(ICollectorEvent event);

}
