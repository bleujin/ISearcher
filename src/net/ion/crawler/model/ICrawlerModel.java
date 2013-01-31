package net.ion.crawler.model;

import java.util.Collection;

import net.ion.crawler.link.Link;

public interface ICrawlerModel {

    boolean isEmpty();

    Link pop();

    void add(Link origin, Link uri);

    void add(Link origin, Collection<Link> uri);

    Collection<Link> getVisitedURIs();

    Collection<Link> getToVisitURIs();

}
