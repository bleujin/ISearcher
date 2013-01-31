package net.ion.crawler.lucene;

import net.ion.crawler.filter.ILinkFilter;
import net.ion.crawler.link.Link;

public class HTMLLinkFilter implements ILinkFilter {

    public HTMLLinkFilter() {
    }

    public boolean accept(Link link) {
        String path = link.getURI().toLowerCase();
        return path.endsWith(".html") || path.endsWith(".htm") || path.endsWith(".txt");
    }

}
