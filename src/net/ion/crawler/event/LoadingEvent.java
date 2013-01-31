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

import net.ion.crawler.core.ICrawler;
import net.ion.crawler.link.Link;
import net.ion.crawler.parser.PageData;

public class LoadingEvent {

    private ICrawler crawler;
    private Link link;
    private PageData pageData;
    private long loadTime ;

    public LoadingEvent() {
    }

    public LoadingEvent(ICrawler crawler, final Link link, final PageData pageData, final long loadTime) {
        this.crawler = crawler;
        this.link = link;
        this.pageData = pageData;
        this.loadTime = loadTime ;
    }

    public ICrawler getCrawler() {
        return crawler;
    }

    public Link getLink() {
        return link;
    }

    public PageData getPageData() {
        return pageData;
    }

	public long getLoadingTime() {
		return loadTime;
	}
    
}
