package net.ion.isearcher.indexer.write;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;

public abstract class AbstractIWriter implements IWriter {

	public Action insertDocument(MyDocument doc) throws IOException {
		myWriteDocument(doc);
		return Action.Insert ;
	}

	protected abstract void myWriteDocument(MyDocument doc) throws IOException;

}
