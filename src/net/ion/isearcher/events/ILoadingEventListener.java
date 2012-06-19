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

import java.util.EventListener;

/**
 * Defines an interface for additional loading processes
 * before and after the download of the page.
 * 
 * Example: the beforeLoading method can set the link of
 * the timestamp allowing the SimpleHttpClientParser to
 * use the If-Modified-Since HTTP header. This reduces
 * the bandwidth. Setting the timestamp of the link, the
 * afterLoading method has to ensure that if the
 * PageData status is PageData.NOT_MODIFIED, the method
 * has to set the PageData for the parsing process and
 * it's extraction of the links.
 * 
 * The implementing class has to be thread-safe if the
 * {@link net.ion.isearcher.crawler.MultiThreadedCrawler} is used.
 *
 * @author bleujin
 * @version $Revision: 1.1 $
 * @since 1.3
 */
public interface ILoadingEventListener extends EventListener {

    /**
     * Listener can implement it's own before loading process.
     *
     * @param event the event containing the link and the crawler
     */
    void beforeLoading(LoadingEvent event);

    /**
     * Listener can implement it's own after loading process.
     *
     * @param event the event containing the link and the crawler
     */
    void afterLoading(LoadingEvent event);

}
