package net.ion.isearcher.crawler.parser.link;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.ion.framework.parse.html.GeneralParser;
import net.ion.framework.parse.html.HTag;
import net.ion.framework.parse.html.NotFoundTagException;
import net.ion.framework.rope.RopeReader;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.util.ILinkExtractor;
import net.ion.isearcher.crawler.util.LinksUtil;
import net.ion.isearcher.http.URI;
import net.ion.isearcher.http.URIException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LinkExtractorBleujin implements ILinkExtractor {

	private static final transient Log LOG = LogFactory.getLog(LinkExtractorBleujin.class);

	public static String unescapeHtmlCommonsLang(String str) {
		return StringEscapeUtils.unescapeHtml(str);
	}

	private static final String[] CHECK_TAGS = { HTMLElementName.A, HTMLElementName.LINK, HTMLElementName.IMG, HTMLElementName.FRAME, HTMLElementName.IFRAME };

	private static final Map<String, String> DEFINED_TAGS = new HashMap<String, String>();
	static {
		DEFINED_TAGS.put(HTMLElementName.A, "href");
		DEFINED_TAGS.put(HTMLElementName.LINK, "href");
		DEFINED_TAGS.put(HTMLElementName.IMG, "src");
		DEFINED_TAGS.put(HTMLElementName.FRAME, "src");
		DEFINED_TAGS.put(HTMLElementName.IFRAME, "src");
	};

	private static final String EMPTY_ANCHOR = null;

	/**
	 * @see net.ion.isearcher.crawler.util.ILinkExtractor#retrieveURIs(java.lang.String, java.lang.String, net.ion.isearcher.crawler.filter.ILinkFilter)
	 */

	public Collection<Link> retrieveURIs(final Link link, final CharSequence content) {
		return retrieveLinks(link, new RopeReader(content));
	}

	Set<Link> retrieveLinks(final Link link, final CharSequence content) {
		return retrieveLinks(link, new RopeReader(content));
	}

	Set<Link> retrieveLinks(final Link referer, final Reader content) {
		URI base = null;
		try {
			base = new URI(referer.getURI(), false);
		} catch (URIException e) {
			LOG.info("Can't create URI for current link '" + referer + '\'', e);
			return Collections.EMPTY_SET;
		}

		Set<Link> result = new HashSet<Link>(); // 
		try {

			HTag root = GeneralParser.parseHTML(content);
			// if exist.. base href tag.
			if (root.hasChild(HTMLElementName.BASE) && StringUtil.isNotBlank(root.getChild(HTMLElementName.BASE).getAttributeValue("href"))) {
				base = new URI(root.getChild(HTMLElementName.BASE).getAttributeValue("href"), false);
			}

			recursiveParse(referer, root, result, base);

			HTag prefixRoot = root.getPrefixTag();
			Debug.line(prefixRoot) ;
			recursiveParse(referer, prefixRoot, result, base);

		} catch (NotFoundTagException ignore) {
			// ignore.printStackTrace();
		} catch (IOException ignore) {
			ignore.printStackTrace();
		} catch (URIException ignore) {
			ignore.printStackTrace();
		}

		return result;
	}


	private void recursiveParse(final Link referer, HTag tag, Set<Link> result, URI base) throws IOException {

		if (tag == null)
			return;
		if (tag.hasChild()) {
			List<HTag> children = tag.getChildren();
			for (HTag child : children) {
				recursiveParse(referer, child, result, base);
			}
		}

		String linkURI = null;
		StringBuilder linkAnchor = new StringBuilder();
		// value is insensitive
		if (HTMLElementName.META.equals(tag.getTagName()) && "refresh".equalsIgnoreCase(tag.getAttributeValue("http-equiv"))) {
			String refreshUrl = StringUtil.substringAfter(tag.getAttributeValue("content"), "=");
			linkURI = LinksUtil.getURI(base, unescapeHtmlCommonsLang(refreshUrl));
		}

		String tagName = tag.getTagName();

		if (DEFINED_TAGS.containsKey(tagName)) {
			String attrName = DEFINED_TAGS.get(tagName);
			linkURI = LinksUtil.getURI(base, unescapeHtmlCommonsLang(tag.getAttributeValue(attrName)));
			if (HTMLElementName.A.equals(tagName)) { // get inverse linked page name
				linkAnchor.append(tag.getOnlyText() + " ");
				if (tag.hasChild()) {
					List<Element> imgs = tag.getElement().getAllElements(HTMLElementName.IMG);
					for (Element img : imgs) {
						linkAnchor.append(img.getAttributeValue("alt") + " ");
					}
				}

				String titleAttirbute = tag.getAttributeValue("title");
				if (titleAttirbute != null) {
					linkAnchor = linkAnchor.append(titleAttirbute + " ");
				}
			}

		}

		if (linkURI != null) {
			Link newLink = Link.create(referer.getURI(), linkURI, linkAnchor, tagName);
			newLink.setDepth(referer.getDepth() + 1) ;
			result.add(newLink);
		}
	}
}
