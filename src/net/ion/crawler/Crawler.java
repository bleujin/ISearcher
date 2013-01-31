package net.ion.crawler;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ion.crawler.core.AbstractCrawler;
import net.ion.crawler.link.Link;
import net.ion.crawler.model.MaxIterationsModel;
import net.ion.crawler.parser.PageData;
import net.ion.crawler.parser.httpclient.SimpleHttpClientParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.exception.ShutDownException;

public class Crawler extends AbstractCrawler {

	// private static final transient Log LOG = new DebugLog() ;
	// LogFactory.getLog(Crawler.class);

	private String startPage;
	public Crawler() {
		super();
	}

	public Crawler(String name) {
		super(name);
	}


	public void setStartPage(final String server, final String start) {
		this.startPage = server + start;
	}

	private final void init() throws IOException {

		// set the default parser
		if (getParser() == null) {
			Debug.debug("No parser set, defaulting to SimpleHttpClientParser.");
			setParser(new SimpleHttpClientParser());
		}

		// set default crawler model
		if (getModel() == null) {
			Debug.debug("No model set, defaulting to MaxIterationsModel.");
			setModel(new MaxIterationsModel());
		}

		if (getAuth() == null) {
			Debug.debug("No Auth set.");
		}

		if (StringUtil.isBlank(this.startPage)) {
			Debug.warn("not setted startPage") ;
		} else {
			getModel().add(Link.Top, Link.createStart(this.startPage));
		}		
		authProgress(getParser());
	}

	/**
	 * Starts the crawling process in a single thread.
	 * 
	 * Before starting the crawling process, the model and the parser have to be set.
	 * 
	 * @see net.ion.isearcher.crawler.core.ICrawler#collect()
	 */
	public final void collect() {

		try {
			fireStart();
			init();
			while (!getModel().isEmpty()) {

				if (isShutDownState()) {
					throw ShutDownException.throwIt(this.getClass());
				}

				// remove a link from the stack
				Link link = getModel().pop();

				fireBeforeLoadingEvent(link);
				long loadStart = System.nanoTime();
				// load the page
				PageData pageData = getParser().load(link);

				long loadEnd = System.nanoTime();
				fireAfterLoadingEvent(link, pageData, loadEnd - loadStart);

				if (isPageDataOK(pageData)) {
					// get the links in the page

					long parseStart = System.nanoTime();
					
					final Collection<Link> parsedLinks = getParser().parse(pageData);
					long parseEnd = System.nanoTime();

					
					fireParserEvent(link, pageData, parsedLinks, parseEnd - parseStart);

					Set<Link> outLinks = new HashSet<Link>() ;
					for (Link parsedLink : parsedLinks) {
						if (getLinkFilter().accept(parsedLink)) {
							outLinks.add(parsedLink) ;
						}
					}

					// remove already visited URIs from the outgoing links list
					outLinks.removeAll(getModel().getVisitedURIs());

					// the rest of the URIs are new and can be visited
					getModel().add(link, outLinks);
				}
			}
		} catch (ShutDownException ignore) { // TODO when occured exception, what doing ?
			ignore.printStackTrace();
		} catch (IOException ignore) {
			ignore.printStackTrace();
		} finally {
			getParser().shutdown() ;
			fireEnd();
		}
	}
}
