package net.ion.isearcher.crawler.parser;

import java.util.Collection;

import net.ion.isearcher.crawler.link.Link;


/**
 * Defines an interface for the parsers. With the load method it is possible to
 * download different pages and to parse them later in a different thread.
 *
 * @author bleujin
 * @version $Revision: 1.4 $
 */
public interface IParser {

    /**
     * Loads the data of the URI. A crawler can load different URIs at the same
     * time and parse them lately. Hence all necessary information have to be
     * stored in a PageData object. E.g. different threads can download the
     * content of the URI parallel and parse them in a different order.
     *
     * @param link the link of the page
     * @return the page data of the uri or <code>null</code> if preloading the
     *         data failed
     */
    PageData load(Link link);

    /**
     * Parses a PageData object e.g. for links and returns them in a Collection.
     *
     * @param pageData the page data of the page
     * @param linkFilter the filter for the URIs
     * @return a collection of outgoing links in the pageData filtered by the
     *         linkFilter
     */
    Collection<Link> parse(PageData pageData);

    void shutdown() ;
    
}
