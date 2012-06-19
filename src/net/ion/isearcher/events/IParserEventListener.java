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
 * Defines an interface for additional parsers during the parse process.
 * Parsers may generate different PageData object for the same page.
 * 
 * A {@link ParserEvent} is created for the status OK, REDIRECT and 
 * NOT_MODIFIED only.
 * 
 * The implementing class has to be thread-safe if the
 * {@link net.ion.isearcher.crawler.MultiThreadedCrawler} is used.
 *
 * @author bleujin
 * @version $Revision: 1.2 $
 */
public interface IParserEventListener extends EventListener {

    /**
     * Listener can implement it's own parsing process.
     *
     * @param event the event containing the PageData and the crawler
     */
    void parsed(ParserEvent event) ;

}
