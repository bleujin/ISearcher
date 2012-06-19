package net.ion.isearcher.events;

public class IndexEndEvent implements IIndexEvent{
	
	private long startTime ;
	public IndexEndEvent(){
		this.startTime = System.currentTimeMillis() ;
	}
	
	public long getStartTime() {
		return startTime ;
	}

}
