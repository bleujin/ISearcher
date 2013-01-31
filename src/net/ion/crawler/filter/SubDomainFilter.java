package net.ion.crawler.filter;

import net.ion.crawler.link.Link;

public class SubDomainFilter implements ILinkFilter {

	private String domain;

	public SubDomainFilter(String domain) {
		this.domain = domain + (domain.endsWith("/") ? "" : "/");
	}

	public boolean accept(Link link) {
		String origin = link.getReferer();
		String mdomain = (domain.startsWith("http://") ? domain.substring(HTTP_PROTOCOL_LENGTH) : domain).toLowerCase();

		String morigin = (origin + (origin.endsWith("/") ? "" : "/")).toLowerCase();

		return morigin.endsWith(mdomain);
	}

}
