package net.ion.isearcher.crawler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.ion.isearcher.crawler.link.Link;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MaxIterationsModel implements ICrawlerModel {

	private static final transient Log LOG = LogFactory.getLog(MaxIterationsModel.class);

	/** The default number of iterations. */
	public static final int DEFAULT_MAX_ITERATIONS = 32;

	/** The max iterations. */
	private int iterations;

	/** A map of the visited links. */
	private HashMap<String, Link> visitedURIs = new HashMap<String, Link>();

	/** A map of the missed visited links. */
	private HashMap<String, Link> toVisitURIs = new HashMap<String, Link>();

	public MaxIterationsModel() {
		this(DEFAULT_MAX_ITERATIONS);
	}

	public MaxIterationsModel(int iterations) {
		this.iterations = iterations;

		LOG.debug("Crawler model: " + MaxIterationsModel.class.getName());
		LOG.debug("- max iterations=" + iterations);
	}

	public synchronized boolean isEmpty() {
		return (toVisitURIs.size() == 0) || (iterations <= 0);
	}

	public synchronized Link pop() {
		// reduce the iterations without a check
		iterations--;

		// remove a link from the stack
		Link link = (Link) toVisitURIs.values().iterator().next();
		toVisitURIs.remove(link.getURI());

		// mark this link as visited
		visitedURIs.put(link.getURI(), link);

		// return the URI
		return link;
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(Link origin, Link uri) {
		HashSet<Link> child = new HashSet<Link>();
		child.add(uri) ;
		add(origin, child);
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(Link origin, Collection<Link> uri) {
		// in this crawler type we can ignore the originUri
		// the rest of the links can be visited (but avoid double entries via
		// the HashSet)
		Iterator<Link> iter = uri.iterator();
		while (iter.hasNext()) {
			addInternal(origin, iter.next());
		}
	}

	public Collection<Link> getVisitedURIs() {
		return visitedURIs.values();
	}

	public Collection<Link> getToVisitURIs() {
		return toVisitURIs.values();
	}

	/** HashMap to avoid that links are added more than once. */
	private HashMap<String, Link> foundLinks = new HashMap<String, Link>();

	private synchronized void addInternal(Link origin, Link link) {
		// find the link via the hashcode
		Link foundLink = (Link) foundLinks.get(link.getURI());

		// is the link new
		if (foundLink == null) {
			foundLinks.put(link.getURI(), link);
			toVisitURIs.put(link.getURI(), link);
		}
	}


	public String toString(){
		return getClass().getName() + "[Iteration:" + iterations + "]" ;
	}
}
