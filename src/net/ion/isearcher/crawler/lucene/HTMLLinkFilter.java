package net.ion.isearcher.crawler.lucene;

import net.ion.isearcher.crawler.filter.ILinkFilter;
import net.ion.isearcher.crawler.link.Link;

public class HTMLLinkFilter implements ILinkFilter {

    public HTMLLinkFilter() {
    }

    public boolean accept(Link link) {
        String path = link.getURI().toLowerCase();
        return path.endsWith(".html") || path.endsWith(".htm") || path.endsWith(".txt");
    }

}
