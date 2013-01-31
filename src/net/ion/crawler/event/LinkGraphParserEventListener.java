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
package net.ion.crawler.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.ion.crawler.link.Link;
import net.ion.crawler.link.LinkGraph;
import net.ion.crawler.parser.PageData;
import net.ion.nsearcher.index.event.IParserEventListener;


/**
 * Creates a link graph of the links. The parser event listener creates {@link LinkGraph}
 * objects which provide a graph of in and out coming links of each downloaded page.
 *
 * @author bleujin
 * @version $Revision: 1.4 $
 */
public class LinkGraphParserEventListener implements IParserEventListener {

    /** the origin of the link graph. */
    private Link origin;


    public Link getOrigin() {
        return origin;
    }

    private HashMap<Link, Collection<Link>> events = new HashMap<Link, Collection<Link>>();


    public Collection<Link> getLinks() {
        return events.keySet();
    }

    /**
     * @param link the URI of the link to be returned
     * @return a link graph of the URI or null if link doesn't exists in the graph
     */
    public LinkGraph getLink(Link link) {
    	List<Link> inLinks = new ArrayList<Link>();
    	for (Entry<Link, Collection<Link>> entry : events.entrySet()) {
			for (Link outLink : entry.getValue()) {
				if (outLink.equals(link)) {
					Link inLink = Link.create(entry.getKey().getReferer(), entry.getKey().getURI(), outLink.getAnchor(), entry.getKey().getLinkTagName());
					inLink.setDepth(entry.getKey().getDepth()) ;
					inLinks.add(inLink) ;
				}
			}
		}
        Collection<Link> outLinks = events.get(link);
        if (outLinks == null) outLinks = Collections.EMPTY_SET ;
        
		return LinkGraph.create(link, inLinks, outLinks);
    }

    /**
     * @see net.ion.isearcher.events.IParserEventListener#parsed(net.ion.crawler.event.isearcher.events.ParserEvent)
     */
    public void parsed(ParserEvent event) {
        // only add valid pages to the graph
        final int status = event.getPageData().getStatus();
        if ((status != PageData.OK) && (status != PageData.NOT_MODIFIED)) {
            return;
        }

        if (origin == null) origin = event.getLink() ;
        events.put(event.getLink(), event.getOutLinks()) ;
    }

}
