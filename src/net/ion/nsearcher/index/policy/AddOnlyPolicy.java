package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.common.AbDocument.Action;
import net.ion.nsearcher.index.IndexSession;

public class AddOnlyPolicy extends AbstractWritePolicy {

	public Action apply(final IndexSession writer, WriteDocument doc) throws IOException {
		writer.insertDocument(doc);
		return Action.Insert ;
	}

}