package net.ion.isearcher.crawler.cmd;

import java.io.File;
import java.util.Iterator;

import net.ion.isearcher.crawler.Crawler;
import net.ion.isearcher.crawler.core.ICrawler;
import net.ion.isearcher.crawler.filter.ILinkFilter;
import net.ion.isearcher.crawler.filter.ServerFilter;
import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.model.MaxDepthModel;
import net.ion.isearcher.crawler.parser.httpclient.SimpleHttpClientParser;
import net.ion.isearcher.crawler.util.StringUtil;

public class CrawlerCmd {
    
    private static final String USAGE = "Usage: java "
        + "-cp commons-codec-1.3.jar" + File.pathSeparatorChar
        + "commons-httpclient-3.1.jar" + File.pathSeparatorChar
        + "commons-logging-1.1.1.jar" + File.pathSeparatorChar
        + "crawler-1.3.0.jar "
        + CrawlerCmd.class.getName() + " "
        + "[-depth <max depth>] [-iterations <max iterations>] [-<filterserver> <server>] [-<httpproxy> <hostname:port>] "
        + "<URL>";

    /**
     * @param args arguments of the command line
     */
    public static void main(String[] args) throws Exception {

        int depth = MaxDepthModel.DEFAULT_MAX_DEPTH;
        int iterations = MaxDepthModel.DEFAULT_MAX_ITERATIONS;
        String uri = null;
        ILinkFilter linkFilter = null;
        String proxyHost = null;
        int proxyPort = -1;

        // get command line parameters
        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1) { // get uri
                uri = args[i];
            } else if ("-depth".equals(args[i])) { // parse -depth option
                depth = Integer.parseInt(args[++i]);
            } else if ("-iterations".equals(args[i])) { // parse -iterations option
                iterations = Integer.parseInt(args[++i]);
            } else if ("-filterserver".equals(args[i])) { // parse -filterserver option
                linkFilter = new ServerFilter(args[++i]);
            } else if ("-httpproxy".equals(args[i])) { // parse -httpproxy option
                final String httpproxy = args[++i];
                final int colon = httpproxy.indexOf(':');
                if (colon >= 0) {
                    proxyHost = httpproxy.substring(0, colon);
                    proxyPort = Integer.parseInt(httpproxy.substring(colon + 1));
                } else {
                    proxyHost = httpproxy;
                }
            } else {
                System.err.println(USAGE);
                System.exit(1);
            }
        }

        if (!StringUtil.hasLength(uri)) {
            System.err.println(USAGE);
            System.exit(1);
        }

        final SimpleHttpClientParser parser = new SimpleHttpClientParser();
        if (proxyHost != null) {
            parser.setProxy(proxyHost, proxyPort);
        }

        // TODO support MultiThreadedCrawler later
        final ICrawler crawler = new Crawler();
        crawler.setParser(parser);
        crawler.setModel(new MaxDepthModel(depth, iterations));
        crawler.setLinkFilter(linkFilter);

        crawler.getModel().add(Link.Top, Link.createStart(uri));
        crawler.collect();

        // show visited links
        Iterator vList = crawler.getModel().getVisitedURIs().iterator();
        while (vList.hasNext()) {
            final Link link = (Link) vList.next();
            System.out.println(link.getURI());
        }
        // show not visited links
        Iterator nvList = crawler.getModel().getToVisitURIs().iterator();
        while (nvList.hasNext()) {
            final Link link = (Link) nvList.next();
            System.out.println(link.getURI());
        }

    }

}
