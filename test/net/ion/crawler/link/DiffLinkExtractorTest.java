/*
 * Project: ExampleLinkExtractor
 * 
 * $Id: DiffLinkExtractorTest.java,v 1.6 2012/06/15 08:35:00 bleujin Exp $
 */
package net.ion.crawler.link;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.ion.crawler.Crawler;
import net.ion.crawler.core.AbstractCrawler;
import net.ion.crawler.event.LinkGraphParserEventListener;
import net.ion.crawler.filter.FileExtensionFilter;
import net.ion.crawler.filter.ILinkFilter;
import net.ion.crawler.filter.LinkFilterUtil;
import net.ion.crawler.filter.ServerFilter;
import net.ion.crawler.model.MaxDepthModel;
import net.ion.crawler.parser.filesystem.FileSystemParser;
import net.ion.crawler.parser.link.LinkExtractorBleujin;
import net.ion.crawler.util.ExampleUtil;
import net.ion.crawler.util.ILinkExtractor;
import net.ion.crawler.util.LinksUtil;
import net.ion.nsearcher.ISTestCase;


/**
 * Example for file system parser and different link extractors which are compared to each other.
 * 
 * Description: Crawls through a web site which is stored on a local file system
 * Result: a similar graph output like a web site and the result which link extractor is better
 * 
 * @author bleujin
 * @version $Id: DiffLinkExtractorTest.java,v 1.6 2012/06/15 08:35:00 bleujin Exp $
 * @since 1.1
 */
public class DiffLinkExtractorTest extends ISTestCase{
    
	private static final boolean SHOW_DIFF = true;
	private static final boolean SHOW_GRAPH_DETAILS = false;
	
	private static final int LOOPS = 1;
	private static final int maxDepth = 25;

    private static final String SERVER = "http://www.i-on.net";
    private static final String START = "/index.html";
    
    private static final String FILE_PATH = ExampleUtil.getWwwSourcePath();

    private static final String[] FILE_EXTENTION_LIST = {
    	".htm", ".html", "/"
    };

    private static final ILinkFilter SERVER_FILTER = new ServerFilter(SERVER);
    private static final ILinkFilter FILE_EXTENTION_FILTER = new FileExtensionFilter(FILE_EXTENTION_LIST);
    
    // the implemented ILinkExtractor examples to be compared
    private static final ILinkExtractor[] LINK_EXTRACTORS = {
        //new LinkExtractorUtilV100(),
        new LinkExtractorBleujin(),
        LinksUtil.DEFAULT_LINK_EXTRACTOR 
        //new LinkExtractorTed(),
        //new LinkExtractorFranz(),
        //new LinkExtractorKonstantinos(),
        //new LinkExtractorMikhail()
    };

    public void testDiff() throws Exception{
    	
    	final int maxLinkExtractors = LINK_EXTRACTORS.length;
    	
    	final Set hitList = new TreeSet(new LinkExtractorComparator());
    	
    	// final MultiThreadedCrawler[] crawlers = new MultiThreadedCrawler[maxLinkExtractors];
    	final Crawler[] crawlers = new Crawler[maxLinkExtractors];
    	final FileSystemParser[] fileSystemParsers = new FileSystemParser[maxLinkExtractors];
    	final LinkGraphParserEventListener[] linkGraphParserEventListeners = new LinkGraphParserEventListener[maxLinkExtractors];
    	
    	final long[] perf = new long[maxLinkExtractors];
        for (int i = 0; i < maxLinkExtractors; i++) {
    		perf[i] = 0;
    	}
        
        for (int k = LOOPS; k > 0; k--) {
			for (int m = 0; m < maxLinkExtractors; m++) {
        		// crawlers[i] = new MultiThreadedCrawler(1, 1);
        		crawlers[m] = new Crawler();
        		crawlers[m].setModel(new MaxDepthModel(maxDepth));
        		crawlers[m].setLinkFilter(LinkFilterUtil.and(SERVER_FILTER, FILE_EXTENTION_FILTER));
            	
        		fileSystemParsers[m] = new FileSystemParser();
        		fileSystemParsers[m].addMapping(SERVER, new File(FILE_PATH));
        		fileSystemParsers[m].setLinkExtractor(LINK_EXTRACTORS[m]);
            	crawlers[m].setParser(fileSystemParsers[m]);
                
            	linkGraphParserEventListeners[m] = new LinkGraphParserEventListener();
                crawlers[m].addParserListener(linkGraphParserEventListeners[m]);
                
                showLinkExtractor(fileSystemParsers[m]);
                
                // run crawler
                long start = System.currentTimeMillis();
                crawlers[m].setStartPage(SERVER, START);
                crawlers[m].collect() ;
                perf[m] += System.currentTimeMillis() - start;
                
                // add to hit list
                hitList.add(crawlers[m]);
                
                if (SHOW_GRAPH_DETAILS) reportPathCheckFile(crawlers[m], linkGraphParserEventListeners[m], System.out);
                showLinks(crawlers[m].getModel().getVisitedURIs(), "Links visited");
                showLinks(crawlers[m].getModel().getToVisitURIs(), "Links NOT visited");
        		System.out.println("");
        	}
        	
        	if (k > maxDepth) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
        	}
        }

    	if (SHOW_DIFF) {
        	for (int i = 0; i < maxLinkExtractors; i++) {
            	for (int j = 0; j < maxLinkExtractors; j++) {
            		if (i != j) {
            	        showDiffLinks(crawlers[i], crawlers[j]);
            		}
            	}
        		
        	}
    	}

		System.out.println("");
        System.out.println("Perf List:");
        for (int i = 0; i < maxLinkExtractors; i++) {
    		System.out.println(LINK_EXTRACTORS[i].getClass().getName() + "=" + perf[i] + "ms");
    	}
    	
       	showHitList(hitList);
    }

	private static void showLinkExtractor(FileSystemParser fileSystemParser) {
		System.out.println("");
        System.out.println("Link Extractor=" + fileSystemParser.getLinkExtractor().getClass().getName());
		System.out.println("");
	}

	private static void showLinks(Collection links, String text) {
		System.out.println("");
        System.out.println(text + "=" + links.size());

        Iterator listVisited = links.iterator();
        while (listVisited.hasNext()) {
            System.out.println(listVisited.next());
        }
	}

    private static void showDiffLinks(AbstractCrawler crawler1, AbstractCrawler crawler2) {
    	Collection link1 = crawler1.getModel().getVisitedURIs();
    	String name1 = ((FileSystemParser) crawler1.getParser()).getLinkExtractor().getClass().getName();
    	Collection link2 = crawler2.getModel().getVisitedURIs();
    	String name2 = ((FileSystemParser) crawler2.getParser()).getLinkExtractor().getClass().getName();
		System.out.println("");
        System.out.println("Diff=" + name1 + "(" + link1.size() + ") vs. " + name2 + "("+ link2.size() + ")");

        TreeSet treeSet1 = new TreeSet(link1);
        TreeSet treeSet2 = new TreeSet(link2);

        Iterator itr1 = treeSet1.iterator();
        while (itr1.hasNext()) {
        	Link l = (Link) itr1.next();
        	if (!treeSet2.contains(l)) {
                System.out.println("Link missing in ("+name2+"): " + l);
        	}
        }
        
    }

	private static void showHitList(Set hitList) {
		System.out.println("");
        System.out.println("Hit List:");
        
        Iterator itr = hitList.iterator();
        while (itr.hasNext()) {
        	AbstractCrawler crawler = (AbstractCrawler) itr.next();
        	int size = crawler.getModel().getVisitedURIs().size();
        	String name = ((FileSystemParser) crawler.getParser()).getLinkExtractor().getClass().getName();
        	System.out.println(size + ": " + name);
        }
        
	}
	
    private static final class LinkExtractorComparator implements Comparator {

    	public int compare(Object o1, Object o2) {
    		AbstractCrawler c1 = (AbstractCrawler) o1;
    		AbstractCrawler c2 = (AbstractCrawler) o2;
    		
    		int v1 = c1.getModel().getVisitedURIs().size();
    		int v2 = c2.getModel().getVisitedURIs().size();
    		
    		if (v1 != v2) {
    			return v2 - v1;
    		}
            
    		int n1 = c1.getModel().getToVisitURIs().size();
    		int n2 = c2.getModel().getToVisitURIs().size();
    		
    		if (n1 != n2) {
        		return n2 - n1;
    		}

    		String name1 = ((FileSystemParser) c1.getParser()).getLinkExtractor().getClass().getName();
    		String name2 = ((FileSystemParser) c2.getParser()).getLinkExtractor().getClass().getName();
    		
    		return name1.compareTo(name2);
       }
        
    }    
    
}

// Hit List of 2008-03-27:
// 26: net.ion.icss.crawler.examples.util.ExampleLinkExtractorTed
// 25: net.ion.icss.crawler.examples.util.ExampleLinkExtractorFranz
// 17: net.ion.icss.crawler.examples.util.ExampleLinkExtractorLinksUtilV100
// 17: net.ion.icss.crawler.util.LinksUtil$1
// 15: net.ion.icss.crawler.examples.util.ExampleLinkExtractorKonstantinos
// 13: net.ion.icss.crawler.examples.util.ExampleLinkExtractorMikhail
