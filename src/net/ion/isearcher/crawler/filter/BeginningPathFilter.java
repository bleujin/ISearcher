package net.ion.isearcher.crawler.filter;

import java.util.Collection;
import java.util.Iterator;

import net.ion.isearcher.crawler.link.Link;

public class BeginningPathFilter implements ILinkFilter {

	private String[] acceptList;

	public BeginningPathFilter(String path) {
		acceptList = new String[] { path.toLowerCase() };
	}

	public BeginningPathFilter(String[] acceptList) {
		this.acceptList = new String[acceptList.length];
		for (int i = 0; i < acceptList.length; i++) {
			this.acceptList[i] = acceptList[i].toLowerCase();
		}
	}

	public BeginningPathFilter(Collection<String> acceptList) {
		this.acceptList = new String[acceptList.size()];

		Iterator<String> itr = acceptList.iterator();
		for (int i = 0; itr.hasNext(); i++) {
			this.acceptList[i] = (itr.next()).toLowerCase();
		}
	}

	public boolean accept(Link link) {
		if (link == null) {
			return false;
		}

		String checkLink = link.getURI().toLowerCase();

		// remove server from link
		if (!checkLink.startsWith("http://") && checkLink.startsWith("http:/")) {
			checkLink = checkLink.substring(5);
		} else if (checkLink.startsWith("http://")) {
			checkLink = checkLink.substring(HTTP_PROTOCOL_LENGTH);
			int i = checkLink.indexOf('/', 0);
			if (i < 0) {
				checkLink = "/";
			} else {
				checkLink = checkLink.substring(i);
			}
		}

		for (int i = 0; i < acceptList.length; i++) {
			if (checkLink.startsWith(acceptList[i])) {
				return true;
			}
		}

		return false;
	}

}
