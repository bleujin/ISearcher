package net.ion.crawler.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ion.crawler.link.Link;

public class RegularExpressionFilter implements ILinkFilter {

	private Pattern pattern;

	public RegularExpressionFilter(String regex) {
		pattern = Pattern.compile(regex);
	}

	public boolean accept(Link link) {
		Matcher m = pattern.matcher(link.getURI());
		return m.matches();
	}

}
