package net.ion.crawler.util;

import java.util.Collection;

import net.ion.crawler.link.Link;

public interface ILinkExtractor {

    Collection<Link> retrieveURIs(Link link, CharSequence content);
}
