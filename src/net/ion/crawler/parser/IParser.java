package net.ion.crawler.parser;

import java.util.Collection;

import net.ion.crawler.link.Link;

public interface IParser {

    PageData load(Link link);

    Collection<Link> parse(PageData pageData);

    void shutdown() ;
    
}
