package net.ion.nsearcher.problem;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.RandomUtil;

import junit.framework.TestCase;

public class TestBlockingQueue extends TestCase {

	
	public void testPutTake() throws Exception {
		final ArrayBlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(100);
		final ScheduledExecutorService exec = Executors.newScheduledThreadPool(5);
		
		exec.submit(new Callable<Void>(){
			private int index = 0 ;
			public Void call() {
				try {
					queue.put(new Event(++index)) ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				exec.schedule(this, RandomUtil.nextInt(10) + 90, TimeUnit.MILLISECONDS) ;
				Debug.debug(index) ;
				return null ;
			}
		}) ;

		
		exec.submit(new Callable<Void>(){
			public Void call() throws Exception {
				int minNo = Integer.MAX_VALUE ;
				int maxNo = Integer.MIN_VALUE ;
				while(queue.size() > 0){
					Event event = queue.take();
					if (minNo > event.no()) minNo = event.no() ;
					if (maxNo < event.no()) maxNo = event.no() ;
				}
				
				Debug.line(minNo, maxNo) ;
				exec.schedule(this, 1000, TimeUnit.MILLISECONDS) ;
				return null;
			}
		}) ;
		
		new InfinityThread().startNJoin() ;
	}
	
}

class Event {
	private int no ;
	public Event(int no){
		this.no = no ;
	}
	
	public int no(){
		return no ;
	}
}