package net.ion.crawler.filter;

import java.util.Collection;

import net.ion.crawler.link.Link;
import net.ion.framework.util.StringUtil;

public class FileExtensionFilter implements ILinkFilter {

	/** the accept list of links. */
	private String[] acceptList;


	public FileExtensionFilter(String[] acceptList) {
		this.acceptList = new String[acceptList.length];
		for (int i = 0; i < acceptList.length; i++) {
			this.acceptList[i] = acceptList[i].toLowerCase();
		}
	}


	public FileExtensionFilter(Collection<String> acceptList) {
		this(acceptList.toArray(new String[0]));
	}

	public FileExtensionFilter(String fileExtension) {
		this(new String[] { fileExtension });
	}

	public boolean accept(Link link) {
		String linkPath = link.getURI() ; 
		int end = StringUtil.indexOf(linkPath, "?");
		String path = StringUtil.substring(linkPath, 0, end > 0 ? end : linkPath.length()).toLowerCase();

		for (int i = 0; i < acceptList.length; i++) {
			if (path.endsWith(acceptList[i])) {
				return true;
			}
		}

		return false;
	}

}
