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
package net.ion.isearcher.crawler.parser.link;

import java.net.URI;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;

import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.util.ILinkExtractor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Example of a LinkExtractor by Franz M.
 * 
 * Open Issue: can't find http:/slash.htm
 * 
 * @author Franz M. and bleujin
 * @version $Revision: 1.4 $
 */
public class LinkExtractorFranz implements ILinkExtractor {

	private static final transient Log log = LogFactory.getLog(LinkExtractorFranz.class);

	public static String getBaseUrl(URI uri) {
		return uri.getScheme() + "://" + uri.getHost() + "/";
	}

	/**
	 * @see net.ion.isearcher.crawler.util.ILinkExtractor#retrieveURIs(java.lang.String, java.lang.String, net.ion.isearcher.crawler.filter.ILinkFilter)
	 */
	public Collection<Link> retrieveURIs(Link referer, CharSequence content) {
		String strUrl = referer.getURI() ;
		String contentForComparasion = content.toString().toLowerCase();

		Collection<Link> result = new HashSet<Link>();

		try {
			URI uri = new URI(strUrl);
			String baseUri = getBaseUrl(uri);
			uri = new URI(baseUri);

			Enumeration en = GlobalProperties.getLinkableTags();
			while (en.hasMoreElements()) {
				final String tag = (String) en.nextElement();
				int index = 0;
				while ((index = contentForComparasion.indexOf(tag, index)) != -1) {
					if ((index = contentForComparasion.indexOf(GlobalProperties.getAttributeOfTag(tag), index)) == -1)
						break;
					if ((index = contentForComparasion.indexOf('=', index)) == -1)
						break;
					String remaining = contentForComparasion.substring(++index);
					StringTokenizer st = new StringTokenizer(remaining, "\t\n\r\"'>#");
					String strLink = st.nextToken();

					// make URI absolute
					try {
						URI uri2 = uri.resolve(strLink);
						strLink = uri2.toString();
						if (strLink != null) {
							result.add(Link.test(strUrl, strLink));
						}
					} catch (IllegalArgumentException e) {
						log.warn("Link violates RFC 2396 which occured in site: " + strUrl + "\n with link: " + strLink);
					}
				}
			}
		} catch (Exception e) {
			log.warn("Unexpected exception occured in site: " + strUrl, e);
		}
		return result;
	}

	/**
	 * TO DOs - static init of default properties - constructor with no params - constructor with Properties param
	 */
	private static final class GlobalProperties {

		private static Hashtable linkableTags = null;

		private static void initLinkableTags() {
			Hashtable tags = new Hashtable(3);

			tags.put("<a ", "href");
			tags.put("<frame ", "src");
			tags.put("<iframe ", "src");
			tags.put("<form ", "action");
			tags.put("<area ", "href");
			tags.put("<link ", "href");
			tags.put("<img ", "src");
			tags.put("<script ", "src");

			// tags.put("a",new String[]{"href"});
			// tags.put("area",new String[]{"href"});
			// tags.put("form",new String[]{"action"});
			// tags.put("frame",new String[]{"src"});
			// tags.put("iframe",new String[]{"src"});
			// tags.put("link",new String[]{"href"});
			// tags.put("img",new String[]{"src"});
			// tags.put("script",new String[]{"src"});

			linkableTags = tags;
		}

		public static Enumeration getLinkableTags() {
			if (linkableTags == null) {
				initLinkableTags();
			}
			return linkableTags.keys();
		}

		public static String getAttributeOfTag(String tagName) {
			return (String) linkableTags.get(tagName);
		}
	}
}
