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


/**
 * Example of a LinkExtractor by Ted M.
 *
 * @author     Ted M. and bleujin
 * @version    $Revision: 1.4 $
 */
public class LinkExtractorTed implements ILinkExtractor {

	private static final String[][] TAGS = {
		{ "<a", "href=\"" },
		{ "<frame ", "src=\"" },
		{ "<iframe ", "src=\"" }
	};

	/**
	 * @see net.ion.isearcher.crawler.util.ILinkExtractor#retrieveURIs(java.lang.String, java.lang.String, net.ion.isearcher.crawler.filter.ILinkFilter)
	 */
	public Collection<Link> retrieveURIs(Link referer, CharSequence content) {
		// FIXME performance and memory!!!
		String pageLower = content.toString().toLowerCase();

		Collection<Link> result = new HashSet<Link>();
		String url = referer.getURI() ;

		// FIXME performance with reg expression?
		// TODO image links extraction

		// find links
		for (int i = 0; i < TAGS.length; i++) {
			final String tag = TAGS[i][0];
			final String attribute = TAGS[i][1];
			int pos = 0;
			
			while (pos < content.length()) {
				int begin = pageLower.indexOf(tag, pos);
				if (begin > -1) {
					int start = pageLower.indexOf(attribute, begin);
					if (start > -1) {
						// create a full qualified link
						start += attribute.length();
						int end = pageLower.indexOf("\"", start);
						String link = LinksUtil.getURI(url, pageLower.substring(start, end));

						// if no filter is set or a set filter accepts the link, then add it to the list
						if (link != null) {
							result.add(Link.test(url, link));
						}

						// next parsing position
						pos = end + 1;
					} else {
						// ignore a tag, because tag wasn't found
						pos = begin + 1;
					}
				} else {
					// end parsing
					pos = content.length();
				}
			}

		}
		
		return result;
	}

}
