package net.ion.isearcher.crawler.util;

import java.util.Collection;

import net.ion.isearcher.crawler.link.Link;

public interface ILinkExtractor {

    Collection<Link> retrieveURIs(Link link, CharSequence content);
}
