package net.ion.isearcher.crawler.filter;

import net.ion.isearcher.crawler.link.Link;

public interface ILinkFilter {

	public final static int HTTP_PROTOCOL_LENGTH = "http://".length();

	public final static ILinkFilter INFINITIVE = new ILinkFilter() {
		public boolean accept(Link link) {
			return true;
		}
	};

    boolean accept(Link link);

}
