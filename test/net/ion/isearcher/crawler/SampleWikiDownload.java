package net.ion.isearcher.crawler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.ion.framework.util.Debug;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.crawler.filter.ILinkFilter;
import net.ion.isearcher.crawler.filter.ServerFilter;
import net.ion.isearcher.crawler.listener.DebugListener;
import net.ion.isearcher.crawler.model.MaxIterationsModel;
import net.ion.isearcher.crawler.util.ExampleUtil;

public class SampleWikiDownload extends ISTestCase{

	public void testDownload() throws Exception {
		MultiThreadedCrawler crawler = new MultiThreadedCrawler("test", 10, 10) ;
		// Crawler crawler = new Crawler() ;
		// crawler.setModel(new MaxDepthModel(1));
		crawler.setModel(new MaxIterationsModel(1000));
		String SERVER = "http://kiwitobes.com/";
		crawler.setLinkFilter(new ServerFilter(SERVER)) ;
		
    	File dir = new File(ExampleUtil.getDownloadPath()) ;
    	if (! dir.exists()){
    		dir.mkdirs() ;
    	}

    	
    	shutDownThreadStart(crawler) ;
    	
    	
		Map mapping = new HashMap();
		mapping.put(SERVER, dir.getAbsolutePath() + "/");;
		
		// crawler.addListener(new DownloadEventListener(mapping)) ;
		// crawler.addListener(new CountListener(10)) ;
		crawler.addListener(new DebugListener()) ;
		crawler.setStartPage(SERVER, "wiki/") ;
		crawler.collect() ;
		
	}

	private void shutDownThreadStart(final MultiThreadedCrawler crawler) {
		Timer timer = new Timer() ;
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				crawler.shutdown("user stop") ;
				Debug.line("END Crawler");
			}
		}, 20000) ;
		
	}
}
