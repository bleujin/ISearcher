package net.ion.isearcher.crawler.listener;

import java.util.concurrent.atomic.AtomicInteger;

import net.ion.framework.util.Debug;
import net.ion.isearcher.events.ICollectorEvent;
import net.ion.isearcher.events.IParserEventListener;
import net.ion.isearcher.events.ParserEvent;
import net.ion.isearcher.indexer.report.ICollectListener;

public class CountListener implements IParserEventListener, ICollectListener {

	private AtomicInteger count = new AtomicInteger() ;
	private int modValue ;
	private long startTime = 0L ;
	private static int nanoUnit = 1000000000;

	public CountListener() {
		this(100) ;
	}
	
	public CountListener(int i) {
		this.modValue = (i < 1) ? 1 : i ;
	}
	
	public void parsed(ParserEvent event) {
		count.incrementAndGet() ;
		printProgress(); 
		
	}

	private void printProgress() {
		if (count.intValue() <= 1) {
			startTime = System.nanoTime();
		}
		
		if ((count.intValue() % modValue) == 0) {
			long takeTime = System.nanoTime() - startTime;
			Debug.line(count, takeTime/nanoUnit ) ;
			Debug.line((1L * count.longValue() * nanoUnit / (takeTime +  1) )  + " per sec") ;
		}
	}
	public void collected(ICollectorEvent event) {
		if (! event.getEventType().isNormal()) {
			if (event.getEventType().isEnd()) {
				Debug.line("Total Time", count, (System.nanoTime() - startTime) / nanoUnit ) ;
			}
			return ;
		}
		count.incrementAndGet() ;
		printProgress();
	}
	
	public int getCount() {
		return count.intValue() ;
	}
	

}
