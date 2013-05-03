package net.ion.nsearcher.search;

import net.ion.nsearcher.common.ReadDocument;

public interface IAfterDocumentFilter {
	public boolean accept(ReadDocument doc);
}
