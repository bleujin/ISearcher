package net.ion.nsearcher.index;

import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import junit.framework.TestCase;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;

public class TestMultiThreadIndex extends TestCase {
	
	public void testMulti() throws Exception {
		
		ScheduledExecutorService es = Executors.newScheduledThreadPool(50) ;
		ArrayBlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(100000);
		
		PutterThread writer = new PutterThread(es, queue);
		Directory dir = FSDirectory.open(new File("./resource/findex"));
		Central central = CentralConfig.oldFromDir(dir).build() ;
		IndexThread getter = new IndexThread(central, es, queue) ;
		
		es.submit(getter) ;
		es.submit(writer) ;
		
		new InfinityThread().startNJoin() ;
	}
}

class IndexThread implements Callable<Void> {

	
	private ScheduledExecutorService es;
	private ArrayBlockingQueue<Event> queue;
	private Central central;
	public IndexThread(Central central, ScheduledExecutorService es, ArrayBlockingQueue<Event> queue) {
		this.central = central ;
		this.es = es ;
		this.queue = queue ;
	}

	public Void call() throws Exception {
		es.submit(new Callable<Void>() {

			public Void call() throws Exception {
				if (queue.size() > 0){
					central.newIndexer().index(new IndexJob<Void>() {
						public Void handle(IndexSession isession) throws Exception {
							while(queue.size() > 0){
								Event event = queue.take();
								WriteDocument wdoc = MyDocument.newDocument(event.key);
								isession.updateDocument(wdoc) ;
							}
							return null;
						}
					}) ;
				}
				es.schedule(this, 50, TimeUnit.MILLISECONDS) ;
				Debug.line("indexed") ;
				return null;
			}
		}) ;
		return null;
	}
	
}

class PutterThread implements Callable<Void>{

	private BlockingQueue<Event> queue ;
	private ScheduledExecutorService es;
	public PutterThread( ScheduledExecutorService es, BlockingQueue<Event> queue) {
		this.es = es ;
		this.queue = queue;
	}
	public Void call() throws Exception {
		queue.put(new Event(RandomUtil.nextRandomString(10), RandomUtil.nextRandomString(20))) ;
		es.schedule(this, RandomUtil.nextInt(3), TimeUnit.MILLISECONDS) ;
		Debug.line("put") ;
		return null;
	}
	
}

class Event{
	String key ;
	String value ;
	
	public Event(String key, String value){
		this.key = key ;
		this.value = value ;
	}
	
	
	
}
