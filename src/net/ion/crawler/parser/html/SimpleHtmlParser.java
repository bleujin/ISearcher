package net.ion.crawler.parser.html;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.ion.crawler.link.Link;
import net.ion.crawler.parser.IParser;
import net.ion.crawler.parser.PageData;
import net.ion.crawler.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;


public class SimpleHtmlParser implements IParser {

    private static final transient Log LOG = LogFactory.getLog(SimpleHtmlParser.class);

    private NodeFilter nodeFilter;

    public NodeFilter getNodeFilter() {
        return nodeFilter;
    }

    public void setNodeFilter(NodeFilter nodeFilter) {
        this.nodeFilter = nodeFilter;
    }

    public ConnectionManager getConnectionManager() {
        return Parser.getConnectionManager();
    }

    public void setConnectionManager(ConnectionManager manager) {
        Parser.setConnectionManager(manager);
    }

    public PageData load(Link link) {
        String uri = link.getURI();
        // LOG.info("download: " + uri);
        try {
            Parser parser = new Parser(uri);
            
            // TODO Issue 2, a redirect can occured, "when" redirects are enabled
            // Default setting of ConnectionManager.mRedirectionProcessingEnabled is false
            // but the ConnectionManager sets HttpURLConnection.setInstanceFollowRedirects
            // to false, if getRedirectionProcessingEnabled() is true. The default
            // HttpURLConnection.followRedirects is true.
            // Hence the parser follows redirects, if mRedirectionProcessingEnabled is false.
            if (LOG.isDebugEnabled()) {
                LOG.debug("link.URI=" + uri + ", parser.url=" + parser.getURL());
            }

            NodeFilter filter = nodeFilter;
            if (filter == null) {
                // filter = new OrFilter(new NodeClassFilter(LinkTag.class), new
                // NodeClassFilter(FrameSetTag.class));
                filter = new NodeClassFilter(LinkTag.class);
            }
            NodeList list = parser.extractAllNodesThatMatch(filter);

            return new PageDataHtmlParser(link, list);
        } catch (ParserException e) {
            LOG.warn("Failed to load " + uri, e);
            return new PageDataHtmlParser(link, PageData.ERROR);
        }
    }

    public Collection parse(PageData pageData) {
        if (!(pageData instanceof PageDataHtmlParser)) {
            LOG.warn("Type mismatch in " + this.getClass().getName());
            return Collections.EMPTY_SET;
        }

        Collection links = new HashSet(); // use HashSet to avoid duplicates

        // TODO Issue 2, check for redirects

        NodeList list = (NodeList) pageData.getData();
        for (int i = 0; i < list.size(); i++) {
            String link = getLink(list.elementAt(i));
            // TODO check if full qualified links should be created similar to the SimpleHttpClientParser
            // if no filter is set or a set filter accepts the link, then add it
            // to the list
            if (StringUtil.hasLength(link)) {
                links.add(link);
            }
        }

        return links;
    }

    public static String getLink(Node node) {
        if (node instanceof LinkTag) {
            String link = ((LinkTag) node).extractLink();
            int k = link.indexOf('#');
            if (k >= 0) {
                link = link.substring(0, k);
            }
            while (link.endsWith("/")) {
                link = link.substring(0, link.length() - 1);
            }
            return link;
        }
        return null;
    }

    // --- PageData implementation ---
    private static class PageDataHtmlParser extends PageData {

        private NodeList data;


        public PageDataHtmlParser(Link link, NodeList data) {
            super(link, PageData.OK);
            this.data = data;
        }

        public PageDataHtmlParser(Link link, int status) {
            super(link, status);
        }


        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = (NodeList) data;
        }

    }

	public void shutdown() {
		
	}

}
