package net.ion.nsearcher.index.collect;


public class CollectThread extends Thread {

	private ICollector collector;

	public CollectThread(String name, ICollector collector) {
		this.collector = collector;
	}

	public void run() {
		collector.collect();
	}
}
