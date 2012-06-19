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

import java.util.Collection;

import net.ion.isearcher.crawler.core.AbstractCrawler;
import net.ion.isearcher.crawler.core.ICrawler;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.parser.PageData;
import net.ion.isearcher.crawler.util.HashFunction;
import net.ion.isearcher.indexer.collect.ICollector;


/**
 * Parsing event for the listeners. This event is created
 * for the status OK and NOT_MODIFIED only.
 *
 * @author bleujin
 * @version $Revision: 1.4 $
 */
public class ParserEvent extends CollectorEvent {

	private static final long serialVersionUID = -4734555136261138726L;

	/**
     * The crawler that can be attached to the event to specify the event's origin.
     */
    protected AbstractCrawler crawler;
    protected Link link;
    protected PageData pageData;

    /** The outgoing links which are filtered by the crawler filter. */
    protected Collection<Link> outLinks;

	private long parseTime;

    /**
     * No-argument constructor so subtypes can easily test implement 
     * <code>Serializable</code>.
     */
    ParserEvent() {
    }

    /**
     * Creates a parser event object that came from the specified origin.
     *
     * @param crawler a crawler that indicates where this event was fired from. This
     *                value is optional; <code>null</code> can be passed in if the
     *                crawler origin is not required.
     * @param link the link of the PageData
     * @param pageData the page data content of this event.
     * @param outLinks outgoing links of type String
     */
    private ParserEvent(AbstractCrawler crawler, final Link link, final PageData pageData, final Collection<Link> outLinks, final long parseTime) {
        this.crawler = crawler;
        this.link = link;
        this.pageData = pageData;
        this.outLinks = outLinks;
        this.parseTime = parseTime ;
    }

    public final static ParserEvent create(AbstractCrawler crawler, final Link link, final PageData pageData, final Collection<Link> outLinks, final long parseTime) {
    	return new ParserEvent(crawler, link, pageData, outLinks, parseTime) ;
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
     * @return Returns the link.
     */
    public Link getLink() {
        return link;
    }

    /**
     * @return returns the page data content object of this event.
     */
    public PageData getPageData() {
        return pageData;
    }

    public long getEventId(){
    	return HashFunction.hashGeneral(link.getURI()) ; 
    }
    
    public long getEventBody() {
    	Object data = getPageData().getData(); 
    	if (data == null) return 0 ;
		return HashFunction.hashWebContent( (data instanceof CharSequence) ? (CharSequence)data : data.toString()) ;
    }
    
    
    /**
     * Returns the containing links of the page as {@link String}
     * in a {@link Collection}. The links are always full
     * qualified and contain the protocol, server and URI.
     *  
     * @return Returns the outgoing links of type String.
     */
    public Collection<Link> getOutLinks() {
        return outLinks;
    }
	
	public long getParseTime(){
		return this.parseTime ;
	}
	
	public String getCollectorName(){
		return crawler.getCollectName() ;
	}
	
	
	public String toString(){
		return link.toString() ;
	}

	public ICollector getCollector() {
		return crawler;
	}
}
