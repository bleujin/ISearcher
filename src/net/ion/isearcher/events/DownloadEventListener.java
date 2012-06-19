/*
 * Copyright 2005-2009 by bleujin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.ion.isearcher.events;

import java.io.File;
import java.util.Map;

import net.ion.isearcher.crawler.filter.ILinkFilter;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.parser.PageData;
import net.ion.isearcher.crawler.parser.httpclient.PageDataHttpClient;
import net.ion.isearcher.crawler.util.FileUtil;
import net.ion.isearcher.crawler.util.UriFileSystemMapperUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This event listener avoids downloading a HTML page twice (for parsing and for
 * saving to the file system). This reduces bandwidth capacity by 50% and speeds
 * up crawling by the factor of 2.
 *
 * This event listener can only be used with a parser which stores the page data
 * as a string and a crawler which fires parser events. E.g. the
 * SimpleHttpClientParser and the Crawler can be used with this event listener.
 *
 * @see net.ion.isearcher.crawler.parser.httpclient.SimpleHttpClientParser
 * @see net.ion.isearcher.crawler.Crawler
 * @see net.ion.isearcher.crawler.core.AbstractCrawler
 *
 * @author bleujin
 */
public class DownloadEventListener implements IParserEventListener {

    private static final transient Log LOG = LogFactory.getLog(DownloadEventListener.class);

    /** the optional download save filter for the page data. */
    private ILinkFilter saveFilter;

    /** the mapping of the URIs to the file system destination. */
    private UriFileSystemMapperUtil mappingUtil;

    /**
     * Constructor for saving the page data to the file system.
     *
     * @param mapping a map of URIs parts as the key to the file path destination.
     */
    public DownloadEventListener(Map mapping) {
        mappingUtil = new UriFileSystemMapperUtil(mapping);
    }

    /**
     * @return the optional save filter for the downloads.
     * @see net.ion.isearcher.crawler.core.ICrawler#getLinkFilter()
     */
    public ILinkFilter getSaveFilter() {
        return saveFilter;
    }

    /**
     * Allows to set an optional save filter beside the mapping objects.
     * Therefore you can define a complete set of mappings between URIs and file
     * directories, and additional you can define a save filter which returns
     * true only for these URIs which should be saved to disk. When the
     * saveFilter's accept method is invoked the first parameter origin will be
     * always null.
     *
     * @param saveFilter optional save filter for the downloads.
     *
     * @see net.ion.isearcher.crawler.filter.ILinkFilter
     */
    public void setSaveFilter(ILinkFilter saveFilter) {
        this.saveFilter = saveFilter;
    }

    /**
     * {@inheritDoc}
     * @see net.ion.isearcher.events.IParserEventListener#parsed(net.ion.isearcher.events.ParserEvent)
     */
    public void parsed(ParserEvent event) {
        PageData page = event.getPageData();
        // is the page data available and OK?
        if ((page.getStatus() == PageData.OK) || (page.getStatus() == PageData.NOT_MODIFIED)) {
            Link link = page.getLink();
            
            // is additional save filter set and should we save the page data?
            if ((saveFilter == null) || (saveFilter.accept(link))) {
                // get destination of file
                String dest = getDestination(link);
                if (dest.endsWith("/")) return ; // dir list
                if (dest != null) {
                    Object obj = page.getData();
                    if (obj instanceof CharSequence) {
                        // save data to file
                        File file = new File(dest);
                        if (page instanceof PageDataHttpClient) {
                            FileUtil.save(file, (CharSequence) obj, ((PageDataHttpClient) page).getCharSet(), link.getTimestamp());
                        } else {
                            FileUtil.save(file, (CharSequence) obj, null, -1L);
                        }
                    } else {
                        LOG.warn("Page data has to be stored as a string. link=" + link);
                    }
                } else {
                    LOG.warn("No file destination found for link=" + link);
                }
            }
        }
    }

    /**
     * Returns the file path and name of the in the parameter specified Link.
     *
     * @param link the link of the resource
     * @return the file path and name of the link's destination, returns null if
     *         no matching can be found.
     * @see net.ion.isearcher.crawler.parser.httpclient.DownloadHelper#getDestination
     */
    public String getDestination(Link link) {
        if (link == null) {
            return null;
        }
        return mappingUtil.getDestination(link.getURI());
    }

}
