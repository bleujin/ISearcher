package net.ion.nsearcher.index.channel;


public abstract class AbstractRelayChannel<T> implements RelayChannel<T> {
	private String cause ;
	private volatile boolean isEnd = false;

	public void doEnd(String cause) {
		this.cause = cause ;
		this.isEnd = true;
	}

	public boolean isEndMessageOccured() {
		return isEnd;
	}
	
	public String getCause(){
		return cause ;
	}

}
// public synchronized void callEnd() {
// Debug.debug("CALL END") ;
// while(hasMessage()){
// try {
// wait();
// } catch (InterruptedException ex) {
// }
// }
// setEndFlag(true) ;
// notifyAll() ;
// Debug.debug("CALL END AFTER") ;
// }