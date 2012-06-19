package net.ion.isearcher.crawler.model;

import java.util.Collection;
import java.util.NoSuchElementException;

import net.ion.isearcher.crawler.link.Link;

public interface ICrawlerModel {

    boolean isEmpty();

    Link pop();

    void add(Link origin, Link uri);

    void add(Link origin, Collection<Link> uri);

    Collection<Link> getVisitedURIs();

    Collection<Link> getToVisitURIs();

}
