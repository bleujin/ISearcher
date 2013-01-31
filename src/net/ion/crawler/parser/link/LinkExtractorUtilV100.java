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

import java.util.Collection;
import java.util.HashSet;

import net.ion.crawler.http.URI;
import net.ion.crawler.http.URIException;
import net.ion.crawler.link.Link;
import net.ion.crawler.util.ILinkExtractor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Example of a LinkExtractor by bleujin. This link extractors reflects the implementation of the retrieveLinks method of version 1.0.
 * 
 * @author Ted M. and bleujin
 * @version $Revision: 1.6 $
 */
public class LinkExtractorUtilV100 implements ILinkExtractor {

	private static final transient Log log = LogFactory.getLog(LinkExtractorUtilV100.class);

	/**
	 * @param currentLink
	 *            the current page in which the new link is contained
	 * @param newLink
	 *            the to be completed link
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
	 * @see net.ion.isearcher.crawler.util.ILinkExtractor#retrieveURIs(java.lang.String, java.lang.String, net.ion.isearcher.crawler.filter.ILinkFilter)
	 */
	public Collection<Link> retrieveURIs(Link referer, CharSequence content) {
		// FIXME performance and memory!!!
		String pageLower = content.toString().toLowerCase();

		Collection<Link> result = new HashSet<Link>();
		String url = referer.getURI() ;
		// find all the links
		int pos = 0;
		while (pos < content.length()) {
			// find a link
			// FIXME performance with reg expression
			// TODO image links extraction
			// TODO <a class="sub" href="../download/meilensteine_bhf_bank.pdf" onclick="return loadPDF(this.href,1);"
			// onkeypress="return loadPDF(this.href);" target="_blank">439KB</a>
			// TODO frame link extraction
			int start = pageLower.indexOf("<a href=\"", pos);
			if (start != -1) {
				int end = pageLower.indexOf('\"', start + 9);

				// create a full qualified link
				String link = LinkExtractorUtilV100.getURI(url, pageLower.substring(start + 9, end));

				// if no filter is set or a set filter accepts the link, then add it to the list
				if (link != null) {
					result.add(Link.test(url, link));
				}

				// next parsing position
				pos = end + 1;
			} else {
				// end parsing
				pos = content.length();
			}
		}

		return result;
	}

}
