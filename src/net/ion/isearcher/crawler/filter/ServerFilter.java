package net.ion.isearcher.crawler.filter;

import net.ion.isearcher.crawler.link.Link;


public class ServerFilter implements ILinkFilter {

	private String server;

	public ServerFilter(String server) {
		this.server = server;
	}

	public boolean accept(Link link) {
		return (link != null) && (link.getURI().startsWith(server));
	}

}
