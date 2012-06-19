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

import net.ion.isearcher.crawler.core.ICrawler;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.parser.PageData;

/**
 * Loading event for the listeners.
 * 
 * Be aware that due to redirects <code>link</code> may
 * not be equal to <code>pageData.getLink()</code>.
 *
 * @author bleujin
 * @version $Revision: 1.1 $
 * @since 1.3
 */
public class LoadingEvent {

    private ICrawler crawler;
    private Link link;
    private PageData pageData;
    private long loadTime ;

    /**
     * No-argument constructor so subtypes can easily implement
     * <code>Serializable</code>.
     */
    public LoadingEvent() {
    }

    /**
     * Creates a parser event object that came from the specified origin.
     *
     * @param crawler a crawler that indicates where this event was fired from. This
     *                value is optional; <code>null</code> can be passed in if the
     *                crawler origin is not required.
     * @param link the link to be loaded.
     * @param pageData the PageData of the link if loaded
     */
    public LoadingEvent(ICrawler crawler, final Link link, final PageData pageData, final long loadTime) {
        this.crawler = crawler;
        this.link = link;
        this.pageData = pageData;
        this.loadTime = loadTime ;
    }

    /**
     * Retrieves the crawler origin of this event, if one was specified. This is
     * most useful when an event handler causes another event to fire - by
     * checking the origin the handler is able to prevent recursive events being
     * fired.
     *
     * @return returns the origin of this event.
     */
    public ICrawler getCrawler() {
        return crawler;
    }

    /**
     * The link which was requested by the crawler.
     * @return the link.
     */
    public Link getLink() {
        return link;
    }

    /**
     * Returns the page data content object of this event. Be aware that due to  
     * redirects <code>link</code> may not be equal to <code>pageData.getLink()</code>.
     * @return the page data content object of this event.
     */
    public PageData getPageData() {
        return pageData;
    }

	public long getLoadingTime() {
		return loadTime;
	}
    
}
