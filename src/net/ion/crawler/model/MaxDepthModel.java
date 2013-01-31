package net.ion.crawler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import net.ion.crawler.link.Link;
import net.ion.crawler.link.LinkDepthComparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MaxDepthModel implements ICrawlerModel {

	private static final transient Log LOG = LogFactory.getLog(MaxDepthModel.class);

	/** The default depth. */
	public static final int DEFAULT_MAX_DEPTH = 4;

	/** The max depth. */
	private int depth;

	/** The default number of iterations. */
	public static final int DEFAULT_MAX_ITERATIONS = 2048 * 2048;

	/** The max iterations. */
	private int iterations;

	/** A map of the visited links. */
	private HashMap<String, Link> visitedURIs = new HashMap<String, Link>();

	/** A set of the to be visited links. */
	private TreeSet<Link> toVisitURIs = new TreeSet<Link>(new LinkDepthComparator());

	public MaxDepthModel() {
		this(DEFAULT_MAX_DEPTH);
	}

	public MaxDepthModel(int depth) {
		this(depth, DEFAULT_MAX_ITERATIONS);
	}

	public MaxDepthModel(int depth, int iterations) {
		this.depth = depth;
		this.iterations = iterations;

		LOG.info("Crawler model: " + MaxDepthModel.class.getName());
		LOG.info("- max depth=" + depth);
		LOG.info("- max iterations=" + iterations);
	}

	public synchronized boolean isEmpty() {
		// check if there is at least one link left
		if ((toVisitURIs.size() == 0) || (iterations <= 0)) {
			return true;
		}

		// get the next element (first element in the set)
		Link l = (Link) toVisitURIs.first();

		return l.getDepth() > depth;
	}

	public synchronized Link pop() {
		// check constraint
		if (toVisitURIs.size() == 0) {
			throw new NoSuchElementException("No more URIs in MaxDepthModel.");
		}

		// reduce the iterations without a check
		iterations--;

		// get the next element and remove it from the list
		Link ldepth = (Link) toVisitURIs.first();
		toVisitURIs.remove(ldepth);

		// check constraint
		if (ldepth.getDepth() > depth) {
			throw new NoSuchElementException("Max depth reached in MaxDepthModel.");
		}

		// mark this link as visited
		visitedURIs.put(ldepth.getURI(), ldepth);

		// return the link
		return ldepth;
	}

	public void add(Link origin, Link uri) {
		HashSet<Link> child = new HashSet<Link>();
		child.add(uri) ;
		add(origin, child);
	}

	public void add(Link origin, Collection<Link> uri) {
		for (Link newUri : uri) {
			newUri.setDepth(origin.getDepth() + 1) ;
			addInternal(origin, newUri);
		}
	}

	public Collection<Link> getVisitedURIs() {
		return visitedURIs.values();
	}

	public Collection<Link> getToVisitURIs() {
		return toVisitURIs;
	}

	/** HashMap to avoid that links are added more than once. */
	private HashMap<String, Link> foundLinks = new HashMap<String, Link>();

	private synchronized void addInternal(Link origin, Link link) {
		// find the link via the hashcode
		Link foundLink = (Link) foundLinks.get(link.getURI());

		// the depth of the uri
		final int depth = origin.getDepth() + 1;

		// is the link new
		if (foundLink == null) {
			link.setDepth(depth) ;
			foundLink = link ;
			foundLinks.put(link.getURI(), foundLink);
			toVisitURIs.add(foundLink);
		} else {
			// check if depth is to change
			// if foundLink.getDepth() == 0 <-- main page authority problem : double check..
			if (depth < foundLink.getDepth() || foundLink.getDepth() == 0) {
				toVisitURIs.remove(foundLink);
				foundLink.setDepth(depth);
				toVisitURIs.add(foundLink);
			}
		}
	}


	public String toString(){
		return getClass().getName() + "[Depth:" + depth + "]" ;
	}
}
