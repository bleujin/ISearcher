package net.ion.nsearcher.search;

import net.ion.nsearcher.common.MyDocument;

public interface IAfterDocumentFilter {
	public boolean accept(MyDocument doc);
}
