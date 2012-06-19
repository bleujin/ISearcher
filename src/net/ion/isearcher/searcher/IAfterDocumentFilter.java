package net.ion.isearcher.searcher;

import net.ion.isearcher.common.MyDocument;

public interface IAfterDocumentFilter {
	public boolean accept(MyDocument doc);
}
