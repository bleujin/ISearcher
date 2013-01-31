/*
 * Copyright 2005-2008 by bleujin
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
package net.ion.crawler.parser.link;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

import net.ion.crawler.http.URI;
import net.ion.crawler.http.URIException;
import net.ion.crawler.link.Link;
import net.ion.crawler.util.ILinkExtractor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;


/**
 * Example of a LinkExtractor by Mikhail K.
 * 
 * Open Issues:
 * 1. doesn't find any frame
 * 2. can't find any iframes
 * 
 * @author Mikhail K. and bleujin
 * @version $Revision: 1.6 $
 */
public class LinkExtractorMikhail implements ILinkExtractor {
    
    private static final transient Log log = LogFactory.getLog(LinkExtractorMikhail.class);
    
    /**
     * @param currentLink the current page in which the new link is contained
     * @param newLink the to be completed link
     * @return a full qualified link or <code>null</code> if the newLink can't be parsed. 
     */
    public static final String getURI(String currentLink, String newLink) {
    	
        if (newLink == null) {
            return null;
        }
        
        try {
            // workaround for http:/path/example.htm
            if (!newLink.startsWith("http://") && newLink.startsWith("http:/")) {
                newLink = newLink.substring(5);
            }
            
            // workaround for https:/path/example.htm
            if (!newLink.startsWith("https://") && newLink.startsWith("https:/")) {
                newLink = newLink.substring(5);
            }
            
            // create new URIs
            // TODO check new URI constructors
            URI base = new URI(currentLink, false);
            URI newURI = new URI(base, newLink, false);
            
            // ignore the schemes other than http
            if (!"http".equals(newURI.getScheme()) && !"https".equals(newURI.getScheme())) {
                return null;
            }
            
            return newURI.toString();
        } catch (URIException e) {
            log.info("URI problem with current link '" + currentLink + "' and new link '" + newLink + '\'', e);
            return null;
        }
    }
    
    /**
     * @param url the url (origin) of the page
     * @param content the complete web page
     * @return a collection of new links
     */
    public Collection<Link> retrieveURIs(Link referer, CharSequence body) {

        Collection<Link> result = new HashSet<Link>();
        String url = referer.getURI() ;
        try {
        	java.net.URI uriLink = new java.net.URI(url);//url of the body
            Parser parser = new Parser();
            parser.setInputHTML(body.toString());
            NodeList  list =  parser.extractAllNodesThatMatch(new NodeClassFilter (LinkTag.class));
            for (int i = 0; i < list.size (); i++){
                LinkTag extracted = (LinkTag)list.elementAt(i);
               
                if(extracted.isJavascriptLink()) continue; //crawler cant do anything with javascript
                if( extracted.isMailLink()) continue;
           
                String extractedLink = extracted.getLink().replaceAll("&amp;", "&");//we need to unescape these
                extractedLink = extractedLink.replaceAll(" ","%20");//URI class doesnt like spaces in URLs, but content creators dont care.. :P
                extractedLink = extractedLink.trim();//URI will barf on a link that look like " foo.com "
               
                if(extractedLink.startsWith("#")) continue; //skip all anchors, they are useless to a crawler
                if(extractedLink.matches ("(?i)^javascript:.*"))continue; //HTMLParser thinks anything but lower case 'javascript:' is a non-js link
               
                java.net.URI resolved = uriLink.resolve(extractedLink);
                //linkfilter will return true if crawling from
                //url --> resolvedUrl
                //is allowed by some rules
                result.add(Link.test(url, resolved.toString()));                   
            }
        } catch (URISyntaxException e) {
            log.info("Bad Link Syntax on page:["+url+"] BAD LINK: "+e.getMessage(), e);
        } catch (ParserException e3) {
        	log.info(e3);
        }
       
        return result;
    }

   
}
