package net.ion.isearcher.crawler.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.http.URI;
import net.ion.isearcher.http.URIException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class LinksUtil {

	private static final transient Log LOG = LogFactory.getLog(LinksUtil.class);

	public static final ILinkExtractor DEFAULT_LINK_EXTRACTOR = new ILinkExtractor() {
		public Collection<Link> retrieveURIs(Link url, CharSequence content) {
			return LinksUtil.retrieveLinks(url, content);
		}
	};

	private LinksUtil() {
	}

	public static String getURI(String currentLink, String newLink) {
		try {
			URI base = new URI(currentLink, false);
			return getURI(base, newLink);
		} catch (URIException e) {
			LOG.info("URI problem with current link '" + currentLink + '\'', e);
			return null;
		}
	}

	public static String getURI(URI currentURI, String newLink) {
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
				newLink = newLink.substring(6);
			}

			// create new URIs
			// TODO check new URI constructors
			// TODO check Issue 1 unescaped links, maybe dependency to apache commons lang can be removed
			URI newURI = new URI(currentURI, newLink, false);

			// ignore the schemes other than http
			if (!"http".equals(newURI.getScheme()) && !"https".equals(newURI.getScheme())) {
				return null;
			}

			return newURI.toString();
		} catch (URIException e) {
			LOG.info("URI problem with current link '" + currentURI.toString() + "' and new link '" + newLink + '\'', e);
			return null;
		}
	}

	public static String unescapeHtmlCommonsLang(String str) {
		return StringEscapeUtils.unescapeHtml(str);
	}

	private static final String[][] TAGS = { 
		{ "<a", "href=\"" }, 
		{ "<frame ", "src=\"" }, 
		{ "<iframe ", "src=\"" } 
		};

	public static Collection<Link> retrieveLinks(final Link referer, final CharSequence content) {
		String url = referer.getURI() ;
		URI base = null;
		try {
			base = new URI(url, false);
		} catch (URIException e) {
			LOG.info("Can't create URI for current link '" + url + '\'', e);
			return Collections.EMPTY_SET;
		}

		// FIXME possible performance and memory improvement
		String pageLower = content.toString().toLowerCase();

		Collection<Link> result = new HashSet<Link>();

		// find links
		for (int i = 0; i < TAGS.length; i++) {
			final String tag = TAGS[i][0];
			final String attribute = TAGS[i][1];
			int pos = 0;

			while (pos < content.length()) {
				final int begin = pageLower.indexOf(tag, pos);
				if (begin > -1) {
					int start = pageLower.indexOf(attribute, begin);
					if (start > -1) {
						// create a full qualified link
						start += attribute.length();
						final int end = pageLower.indexOf('\"', start);
						// Support of HTML escaped links when Apache Commons Lang 2.4 is installed
						String link = LinksUtil.getURI(base, unescapeHtmlCommonsLang(pageLower.substring(start, end)));

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
