/*
 *  Copyright 2005-2008 by bleujin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package net.ion.isearcher.crawler.parser.link;

import java.util.Collection;
import java.util.HashSet;

import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.util.ILinkExtractor;
import net.ion.isearcher.crawler.util.LinksUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;


/**
 * Example of a LinkExtractor by Konstantinos C.
 * 
 * Open Issues:
 * 1. finds only few pages (no frames, no iframes etc.)
 *
 * @author     Konstantinos C. and bleujin
 * @version    $Revision: 1.5 $
 */
public class LinkExtractorKonstantinos implements ILinkExtractor {

	private static final transient Log log = LogFactory.getLog(LinkExtractorKonstantinos.class);

	/**
	 * Retrieve the links from the given page content.
	 * @param url The url (origin) of the page
	 * @param content The complete web page
	 * @return A collection of links accepted from the url
	 */
	public Collection<Link> retrieveURIs(Link referer, CharSequence content) {

		Collection<Link> result = new HashSet<Link>();
		String url = referer.getURI() ;
		try {

			Parser parser;
			if (content == null) {
				parser = new Parser (url);
			} else {
				parser = new Parser(content.toString());
			}
			
			//attempt to extract the encoding
			NodeFilter metaNodeFilter = new NodeClassFilter(MetaTag.class);
			NodeList metaTags = parser.extractAllNodesThatMatch(metaNodeFilter);
			if (metaTags.size() > 0) {
				TagNode m = (TagNode) metaTags.elementAt(0);
				// for each meta tag
				for (SimpleNodeIterator i = metaTags.elements(); i.hasMoreNodes(); m = (TagNode)i.nextNode()) {
					String contentField;
					//if there is a content attribute, get its value
					if ((contentField = m.getAttribute("content")) != null) {
						//should look like text/html; charset=UTF-8 so break it up at the ;
						String[] contentFieldValues = contentField.split("\\s*;\\s*");
						for (int j = 0; j < contentFieldValues.length; j++) {
							//then break each pair up again at the =
							String[] pair = contentFieldValues[j].split("\\s*=\\s*");
							if (pair.length > 1 && pair[0].equalsIgnoreCase("charset")) {
								parser.setEncoding(pair[1]);
								break;
							}
						}						
					}
				}
			}
			
			//parser.setEncoding("iso-8859-7");
			//get all the links
			NodeFilter linkNodeFilter = new NodeClassFilter (LinkTag.class);
			parser.reset(); //TODO: get meta and link tags out at the same time
			NodeList linkTags = parser.extractAllNodesThatMatch (linkNodeFilter);
			TagNode n = (TagNode) linkTags.elementAt(0);
			
			for (SimpleNodeIterator i = linkTags.elements(); i.hasMoreNodes(); n = (TagNode)i.nextNode()) {
				String targetURI = n.getAttribute("href");
				if (targetURI != null ) {
					String link = LinksUtil.getURI(url, targetURI);
					result.add(Link.test(url, convert(link)));
				}
			}

		} catch (ParserException e) {
			log.warn(e);
		}
		
		return result;
	}
	
	/**
	 * An attempt convert links found by the htmlparser library into proper urls that can be
	 * revisited using the htmlparser (ironically)
	 * 
	 * What we want to do is make the following conversions:
	 * 
	 * 1.
	 * &amp; -> &
	 * &quot; -> "
	 * &lt; -> <
	 * &gt; -> >
	 * //TODO whole bunch of other stuff from http://ascii.cl/htmlcodes.htm
	 * 
	 * @author Kostas
	 * @param html The html text to process
	 * @param encoding The encoding to be used, this is currently ignored
	 *
	 */
	
	public static final String convert(String html, String encoding) {
		String result = html.replaceAll("\\&amp;", "&");
		result = result.replaceAll("\\&quot;", "\"");
		result = result.replaceAll("\\&lt;", "<");
		result = result.replaceAll("\\&gt;", ">");
		
		return result;
	}
	
	public static final String convert(String html) {
		return convert(html, "UTF-8");
	}
	
}
