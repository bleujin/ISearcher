package net.ion.isearcher.events;

import net.ion.framework.util.Debug;

public class IndexExceptionEvent implements IIndexEvent {

	private long startTime;
	private Throwable ex;

	public IndexExceptionEvent(Throwable ex) {
		this.ex = ex;
		this.startTime = System.currentTimeMillis();
	}

	public String getExceptionMessage() {
		Throwable iex = ex;
		while (iex != null && iex.getCause() != null) {
			iex = iex.getCause();
			Debug.line('=', iex);
		}
		return iex.getMessage();
	}

	public long getStartTime() {
		return startTime;
	}

}
