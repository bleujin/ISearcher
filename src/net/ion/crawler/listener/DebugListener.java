package net.ion.crawler.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ion.crawler.core.ICrawler;
import net.ion.crawler.event.LoadingEvent;
import net.ion.crawler.event.ParserEvent;
import net.ion.crawler.link.Link;
import net.ion.crawler.model.ICrawlerModel;
import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeWriter;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.index.collect.ICollectListener;
import net.ion.nsearcher.index.event.ICollectorEvent;
import net.ion.nsearcher.index.event.ILoadingEventListener;
import net.ion.nsearcher.index.event.IParserEventListener;
import net.ion.nsearcher.search.processor.LimitedChannel;

public class DebugListener implements ICollectListener, IParserEventListener, ILoadingEventListener {

	private long startTime = 0L;
	private ICrawler crawler = null;
	private long totalTime = 0L ;
	private long totalParseTime = 0L;
	private long totalLoadTime = 0L;
	private LimitedChannel<String> channel = new LimitedChannel<String>(100) ;

	
	private int visitedURISize = 0 ;
	private int toVisitURISize = 0 ;
	public DebugListener() {
	}

	public void collected(ICollectorEvent event) {
		if (! event.getEventType().isNormal()) {
			if (event.getEventType().isBegin()){
				startTime = System.nanoTime() ;
			} else if (event.getEventType().isEnd()) {
				totalTime = System.nanoTime() - startTime ;
				if (Debug.isInfoEnabled() && crawler != null) {
					ICrawlerModel model = crawler.getModel() ;
		            Collection<Link> visitedURIs = model.getVisitedURIs();
		            Collection<Link> toVisitURIs = model.getToVisitURIs();

		            visitedURISize = visitedURIs.size() ;
		            toVisitURISize = toVisitURIs.size() ;
		            
		            report(resultReport().toString()) ;
		            this.crawler = null ; 
		        }				
			}
		}
	}

	private void report(String message){
		synchronized (channel){
			channel.add(message) ;
		}
		Debug.info(message) ;
	}
	
	public void parsed(ParserEvent event) {
		if (crawler == null) crawler = event.getCrawler();
		
		totalParseTime += event.getParseTime();
		report(event.getLink().toString() + " parsed") ; 
	}

	public void afterLoading(LoadingEvent event) {
		report(event.getLink().toString() + " loaded") ; 
		totalLoadTime += event.getLoadingTime();
	}

	public void beforeLoading(LoadingEvent event) {
	}

	public Rope resultReport() {
		RopeWriter rw = new RopeWriter() ;
        rw.write("Visited URIs: " + visitedURISize + "\n");

        if (toVisitURISize > 0) {
        	rw.write("still URIs to be visited, at least: " + toVisitURISize + "\n");
        }

        // output stop watch data
        rw.write(getTime());
        return rw.getRope() ;
	}

	public String getTime() {
		StringBuilder sb = new StringBuilder() ;
        sb.append("Total time: " + (System.nanoTime() - startTime)/1000000 + " ms\n");
        sb.append("- loading:  " + totalLoadTime/1000000 + " ms\n");
        sb.append("- parsing:  " + totalParseTime/1000000 + " ms\n");
		return sb.toString();
	}

	public String[] getCurrentProgress() {
		List<String> result = new ArrayList<String>();
		
		result.add("Current Progress Info ==============") ;
		result.add(getTime());
		
		if (crawler == null) return result.toArray(new String[0]) ;
		
		result.add("visitedURISize :  " + crawler.getModel().getVisitedURIs().size() + " ms");
		result.add("toVisitURISize :  " + crawler.getModel().getToVisitURIs().size() + " ms");
		
		result.add("Recent Message ==============" );
		synchronized (channel) {
			
			for (int i = 0 ; i < 100; i++) {
				String message = channel.poll() ;
				if (message == null) break ;
				result.add(message) ;
			}
		}
		
		return result.toArray(new String[0]) ;
	}


}