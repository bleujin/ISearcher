package net.ion.crawler;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.ion.crawler.core.AbstractCrawler;
import net.ion.crawler.link.Link;
import net.ion.crawler.model.MaxDepthModel;
import net.ion.crawler.parser.PageData;
import net.ion.crawler.parser.httpclient.SimpleHttpClientParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.exception.ShutDownException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class MultiThreadedCrawler extends AbstractCrawler {

	private static final transient Log LOG = LogFactory.getLog(MultiThreadedCrawler.class);

	private final Object updateLock = new Object();

	private ThreadPoolExecutor loadService;
	private ThreadPoolExecutor parseService;

	private AtomicInteger jobsToBeFinished = new AtomicInteger();
	private String startPage;

	public MultiThreadedCrawler() {
		this(DEFAULT_NAME, 6, 3);
	}

	/**
	 * Creates a multi threaded crawler which delegates the load and parse tasks
	 * to different threads. Per RFC 2616 sec 8.1.4 the maximum number of
	 * connections allowed per host is 2.
	 * 
	 * @see org.apache.commons.httpclient.MultiThreadedHttpConnectionManager#DEFAULT_MAX_TOTAL_CONNECTIONS
	 * @param maxLoadThreads
	 *            : maximum of threads for loading the content
	 * @param maxParseThreads
	 *            : maximum of threads for parsing the downloaded content
	 */
	public MultiThreadedCrawler(String name, int maxLoadThreads, int maxParseThreads) {
		super(name);
		loadService = new ThreadPoolExecutor(maxLoadThreads, maxLoadThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
		parseService = new ThreadPoolExecutor(maxParseThreads, maxParseThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
	}

	public void setStartPage(final String server, final String start) {
		this.startPage = server + start;
	}

	private void init() throws IOException {

		// set the default parser
		if (getParser() == null) {
			Debug.debug("No parser set, defaulting to SimpleHttpClientParser.");
			setParser(new SimpleHttpClientParser(true));
		}

		// set default crawler model
		if (getModel() == null) {
			Debug.debug("No model set, defaulting to MaxDepthModel.");
			// TODO remove parameter!
			setModel(new MaxDepthModel(1));
		}

		// add at least one link to the list
		if (StringUtil.isBlank(this.startPage)) {
			Debug.warn("not setted startPage") ;
		} else {
			getModel().add(Link.Top, Link.createStart(this.startPage));
		} 
		authProgress(getParser());

	}

	public void collect() {

		try {
			fireStart();
			init();
			while (!isFinished()) {

				if (isShutDownState()) {
					Debug.debug("SHUTDOWN...");
					throw ShutDownException.throwIt(this.getClass());
				}

				boolean wait = false;
				synchronized (updateLock) {
					if (!getModel().isEmpty() && (! loadService.isShutdown())) {
						// remove a link from the stack
						Link link = getModel().pop();
						jobsToBeFinished.incrementAndGet();
						loadService.execute(new LoadTask(link));
					} else {
						// FIXME is false when one parsing thread is still
						// running but task is empty
						// hence this thread consumes necessary CPU of parsing
						// thread
						wait = parseService.getTaskCount() > 0;
					}
				}

				// FIXME avoid sleeping: wait if model is empty, but parsing is
				// running (BarrierLock)
				if (wait) {
					try {
						// log.info("ttbf=" + jobsToBeFinished + ", ls=" +
						// loadService.getQueue().size() + ", ps=" +
						// parseService.getQueue().size());
						Thread.sleep(50);
					} catch (InterruptedException e) {
						LOG.info("Sleep of " + this.getClass().getName() + " interrupted", e);
					}
				}

			}
		} catch (ShutDownException ignore) { // TODO when occured exception,
			// what doing ?
			ignore.printStackTrace();
		} catch (IOException ignore) {
			ignore.printStackTrace();
		} finally {
			getParser().shutdown() ;
			tryGracefulEnd();
			fireEnd();
		}
	}

	private void tryGracefulEnd() {
		loadService.shutdown();
		parseService.shutdown();

		int retry = 0;
		while (!parseService.isTerminated()) {
			if (retry > 3) {
				loadService.purge();
				parseService.purge();
			} else if (retry > 5) {
				try {
					loadService.awaitTermination(2, TimeUnit.SECONDS);
				} catch (InterruptedException ignore) {
					ignore.printStackTrace();
				}
				try {
					parseService.awaitTermination(2, TimeUnit.SECONDS);
				} catch (InterruptedException ignore) {
					ignore.printStackTrace();
				}
			} else if (retry > 10) {
				loadService.shutdownNow();
				parseService.shutdownNow();
				break;
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			retry++;
		}
	}

	private boolean isFinished() {
		synchronized (updateLock) {
			return getModel().isEmpty() && (jobsToBeFinished.intValue() == 0);
		}
	}

	/**
	 * Task for loading a specific link.
	 */
	private class LoadTask implements Runnable {

		private final Link link;

		public LoadTask(Link link) {
			this.link = link;
			LOG.debug("LoadTask created for link: " + link.getURI());
		}

		/**
		 * {@inheritDoc}
		 */
		public void run() {
			LOG.debug("LoadTask running for link: " + link.getURI());

			try {
				// load the page of the link
				fireBeforeLoadingEvent(link);
				long startTime = System.nanoTime();
				PageData pageData = getParser().load(link);
				long endTime = System.nanoTime();
				fireAfterLoadingEvent(link, pageData, endTime - startTime);

				if (isPageDataOK(pageData)) {
					// no sync needed because we are still running
					jobsToBeFinished.incrementAndGet();
					try {
						parseService.execute(new ParseTask(link, pageData));
					} catch (RejectedExecutionException ignore) {
						Debug.line("CANCELED[Reject]", ignore, ignore.getMessage());
					}
				}
			} finally {
				jobsToBeFinished.decrementAndGet();
			}

			LOG.debug("LoadTask finished for link: " + link.getURI());
		}

	}

	/**
	 * Task for parsing the page data.
	 */
	private class ParseTask implements Runnable {

		private final Link link;
		private final PageData pageData;

		/**
		 * Task for parsing the page data.
		 * 
		 * @param link
		 *            the uri of the page data
		 * @param pageData
		 *            the content of the page
		 */
		public ParseTask(Link link, PageData pageData) {
			this.link = link;
			this.pageData = pageData;
		}

		/**
		 * {@inheritDoc}
		 */
		public void run() {
			LOG.debug("ParseTask running for link: " + link.getURI());

			try {
				// get the links in the page
				long parseStart = System.nanoTime();
				Collection<Link> parsedLinks = getParser().parse(pageData);
				long parseEnd = System.nanoTime();

				// update model
				synchronized (updateLock) {

					fireParserEvent(link, pageData, parsedLinks, parseEnd - parseStart);

					Set<Link> outLinks = new HashSet<Link>();
					for (Link parsedLink : parsedLinks) {
						if (getLinkFilter().accept(parsedLink)) {
							outLinks.add(parsedLink);
						}
					}

					// remove already visited URIs from the new URI list
					outLinks.removeAll(getModel().getVisitedURIs());

					// the rest of the URIs can be visited
					getModel().add(link, outLinks);
				}
			} finally {
				jobsToBeFinished.decrementAndGet();
			}

			LOG.debug("ParseTask finished for link: " + link.getURI());
		}

	}

}
